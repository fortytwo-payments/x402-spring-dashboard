package io.x402.dashboard.seller.web;

import io.x402.dashboard.seller.config.X402DashboardProperties;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.service.X402UsageAggregationService;
import io.x402.dashboard.seller.service.X402UsageEventService;
import io.x402.dashboard.seller.service.dto.OverviewTotals;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Controller for Thymeleaf-based dashboard views.
 */
@Controller
public class X402DashboardController {

    private final X402UsageAggregationService aggregationService;
    private final X402UsageEventService eventService;
    private final X402DashboardProperties props;

    public X402DashboardController(
            X402UsageAggregationService aggregationService,
            X402UsageEventService eventService,
            X402DashboardProperties props) {
        this.aggregationService = aggregationService;
        this.eventService = eventService;
        this.props = props;
    }

    @GetMapping("${x402.dashboard.path:/x402-dashboard}")
    public String index(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

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

        OverviewTotals totals = aggregationService.getOverviewTotals(tenantId, fromTime, toTime);

        model.addAttribute("apiPath", props.getApiPath());
        model.addAttribute("basePath", props.getPath());
        model.addAttribute("activePage", "overview");
        model.addAttribute("totals", totals);
        model.addAttribute("fromDate", fromTime.toLocalDate().toString());
        model.addAttribute("toDate", toTime.toLocalDate().toString());
        model.addAttribute("statusAggregations", aggregationService.aggregateByStatus(tenantId, fromTime, toTime));
        model.addAttribute("dailyData", aggregationService.aggregateByDate(tenantId, fromTime, toTime, null));

        return "x402-dashboard/index";
    }

    @GetMapping("${x402.dashboard.path:/x402-dashboard}/agents")
    public String agents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

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

        model.addAttribute("apiPath", props.getApiPath());
        model.addAttribute("basePath", props.getPath());
        model.addAttribute("activePage", "agents");
        model.addAttribute("agents", aggregationService.aggregateByAgent(tenantId, fromTime, toTime, X402UsageStatus.SUCCESS));
        model.addAttribute("fromDate", fromTime.toLocalDate().toString());
        model.addAttribute("toDate", toTime.toLocalDate().toString());

        return "x402-dashboard/agents";
    }

    @GetMapping("${x402.dashboard.path:/x402-dashboard}/endpoints")
    public String endpoints(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

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

        model.addAttribute("apiPath", props.getApiPath());
        model.addAttribute("basePath", props.getPath());
        model.addAttribute("activePage", "endpoints");
        model.addAttribute("endpoints", aggregationService.aggregateByEndpoint(tenantId, fromTime, toTime, X402UsageStatus.SUCCESS));
        model.addAttribute("fromDate", fromTime.toLocalDate().toString());
        model.addAttribute("toDate", toTime.toLocalDate().toString());

        return "x402-dashboard/endpoints";
    }

    @GetMapping("${x402.dashboard.path:/x402-dashboard}/events")
    public String events(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {

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

        var eventsPage = eventService.findEvents(tenantId, usageStatus, fromTime, toTime, page, size);

        model.addAttribute("apiPath", props.getApiPath());
        model.addAttribute("basePath", props.getPath());
        model.addAttribute("activePage", "events");
        model.addAttribute("events", eventsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventsPage.getTotalPages());
        model.addAttribute("totalElements", eventsPage.getTotalElements());
        model.addAttribute("fromDate", fromTime.toLocalDate().toString());
        model.addAttribute("toDate", toTime.toLocalDate().toString());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", X402UsageStatus.values());

        return "x402-dashboard/events";
    }

    private OffsetDateTime parseDateTime(String dateStr, OffsetDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        try {
            // Try ISO format first
            return OffsetDateTime.parse(dateStr);
        } catch (Exception e) {
            try {
                // Try date-only format
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
