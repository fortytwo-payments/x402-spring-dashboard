package io.x402.dashboard.web;

import io.x402.dashboard.config.X402DashboardProperties;
import io.x402.dashboard.domain.X402UsageEvent;
import io.x402.dashboard.domain.X402UsageStatus;
import io.x402.dashboard.service.X402UsageAggregationService;
import io.x402.dashboard.service.X402UsageEventService;
import io.x402.dashboard.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for dashboard data.
 */
@RestController
@RequestMapping("${x402.dashboard.api-path:/x402-dashboard/api}")
public class X402DashboardRestController {

    private final X402UsageAggregationService aggregationService;
    private final X402UsageEventService eventService;
    private final X402DashboardProperties props;

    public X402DashboardRestController(
            X402UsageAggregationService aggregationService,
            X402UsageEventService eventService,
            X402DashboardProperties props) {
        this.aggregationService = aggregationService;
        this.eventService = eventService;
        this.props = props;
    }

    /**
     * Get overview totals.
     */
    @GetMapping("/overview")
    public ResponseEntity<OverviewTotals> getOverview(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        OffsetDateTime fromTime = parseStartDateTime(from,
                java.time.LocalDate.now().minusDays(7)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        OffsetDateTime toTime = parseEndDateTime(to,
                java.time.LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());

        return ResponseEntity.ok(aggregationService.getOverviewTotals(tenantId, fromTime, toTime));
    }

    /**
     * Get top agents by request count.
     */
    @GetMapping("/agents/top")
    public ResponseEntity<List<AgentAggregation>> getTopAgents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status) {

        OffsetDateTime fromTime = parseStartDateTime(from,
                java.time.LocalDate.now().minusDays(7)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        OffsetDateTime toTime = parseEndDateTime(to,
                java.time.LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        X402UsageStatus usageStatus = status != null ? X402UsageStatus.valueOf(status) : X402UsageStatus.SUCCESS;

        return ResponseEntity.ok(aggregationService.aggregateByAgent(tenantId, fromTime, toTime, usageStatus));
    }

    /**
     * Get top endpoints by request count.
     */
    @GetMapping("/endpoints/top")
    public ResponseEntity<List<EndpointAggregation>> getTopEndpoints(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status) {

        OffsetDateTime fromTime = parseStartDateTime(from,
                java.time.LocalDate.now().minusDays(7)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        OffsetDateTime toTime = parseEndDateTime(to,
                java.time.LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        X402UsageStatus usageStatus = status != null ? X402UsageStatus.valueOf(status) : X402UsageStatus.SUCCESS;

        return ResponseEntity.ok(aggregationService.aggregateByEndpoint(tenantId, fromTime, toTime, usageStatus));
    }

    /**
     * Get aggregation by status.
     */
    @GetMapping("/status")
    public ResponseEntity<List<StatusAggregation>> getStatusAggregation(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        OffsetDateTime fromTime = parseStartDateTime(from,
                java.time.LocalDate.now().minusDays(7)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        OffsetDateTime toTime = parseEndDateTime(to,
                java.time.LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());

        return ResponseEntity.ok(aggregationService.aggregateByStatus(tenantId, fromTime, toTime));
    }

    /**
     * Get daily aggregation for charts.
     */
    @GetMapping("/daily")
    public ResponseEntity<List<DateAggregation>> getDailyAggregation(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status) {

        OffsetDateTime fromTime = parseStartDateTime(from,
                java.time.LocalDate.now().minusDays(30)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        OffsetDateTime toTime = parseEndDateTime(to,
                java.time.LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        X402UsageStatus usageStatus = status != null ? X402UsageStatus.valueOf(status) : null;

        return ResponseEntity.ok(aggregationService.aggregateByDate(tenantId, fromTime, toTime, usageStatus));
    }

    /**
     * Get events with pagination.
     */
    @GetMapping("/events")
    public ResponseEntity<Page<X402UsageEvent>> getEvents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        OffsetDateTime fromTime = parseStartDateTime(from,
                java.time.LocalDate.now().minusDays(7)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        OffsetDateTime toTime = parseEndDateTime(to,
                java.time.LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime());
        X402UsageStatus usageStatus = (status != null && !status.isEmpty()) ? X402UsageStatus.valueOf(status) : null;

        return ResponseEntity.ok(eventService.findEvents(tenantId, usageStatus, fromTime, toTime, page, size));
    }

    /**
     * Get recent events.
     */
    @GetMapping("/events/recent")
    public ResponseEntity<List<X402UsageEvent>> getRecentEvents(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(eventService.getRecent(limit));
    }

    /**
     * Get single event by ID.
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<X402UsageEvent> getEvent(@PathVariable Long id) {
        return eventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private OffsetDateTime parseDateTime(String dateStr, OffsetDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        try {
            return OffsetDateTime.parse(dateStr);
        } catch (Exception e) {
            try {
                return OffsetDateTime.parse(dateStr + "T00:00:00Z");
            } catch (Exception ex) {
                return defaultValue;
            }
        }
    }

    private OffsetDateTime parseStartDateTime(String dateStr, OffsetDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        try {
            // Try ISO format first
            return OffsetDateTime.parse(dateStr);
        } catch (Exception e) {
            try {
                // Try date-only format - start of day in system timezone
                return java.time.LocalDate.parse(dateStr)
                        .atStartOfDay()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime();
            } catch (Exception ex) {
                return defaultValue;
            }
        }
    }

    private OffsetDateTime parseEndDateTime(String dateStr, OffsetDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        try {
            // Try ISO format first
            return OffsetDateTime.parse(dateStr);
        } catch (Exception e) {
            try {
                // Try date-only format - end of day in system timezone
                return java.time.LocalDate.parse(dateStr)
                        .atTime(23, 59, 59)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toOffsetDateTime();
            } catch (Exception ex) {
                return defaultValue;
            }
        }
    }
}
