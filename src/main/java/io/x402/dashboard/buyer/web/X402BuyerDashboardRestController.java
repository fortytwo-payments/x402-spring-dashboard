package io.x402.dashboard.buyer.web;

import io.x402.dashboard.buyer.config.X402BuyerDashboardProperties;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import io.x402.dashboard.buyer.service.X402SpendingAggregationService;
import io.x402.dashboard.buyer.service.X402SpendingEventService;
import io.x402.dashboard.buyer.service.dto.ServiceSpendingAggregation;
import io.x402.dashboard.buyer.service.dto.SpendingOverview;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST API Controller for Buyer Dashboard.
 */
@RestController
@RequestMapping("${x402.buyer.dashboard.api-path:/x402-buyer-dashboard/api}")
@ConditionalOnProperty(name = "x402.buyer.dashboard.enabled", havingValue = "true")
public class X402BuyerDashboardRestController {

    private final X402SpendingAggregationService aggregationService;
    private final X402SpendingEventService eventService;
    private final X402BuyerDashboardProperties properties;

    public X402BuyerDashboardRestController(
            X402SpendingAggregationService aggregationService,
            X402SpendingEventService eventService,
            X402BuyerDashboardProperties properties) {
        this.aggregationService = aggregationService;
        this.eventService = eventService;
        this.properties = properties;
    }

    /**
     * Get spending overview.
     */
    @GetMapping("/overview")
    public ResponseEntity<SpendingOverview> getOverview(
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        String actualBuyerId = buyerId != null ? buyerId : properties.getDefaultBuyerId();
        if (actualBuyerId == null) {
            actualBuyerId = "default";
        }

        OffsetDateTime fromDate = from != null ? OffsetDateTime.parse(from) : OffsetDateTime.now().minusDays(30);
        OffsetDateTime toDate = to != null ? OffsetDateTime.parse(to) : OffsetDateTime.now();

        SpendingOverview overview = aggregationService.getOverview(actualBuyerId, fromDate, toDate);
        return ResponseEntity.ok(overview);
    }

    /**
     * Get top services by spending.
     */
    @GetMapping("/services/top")
    public ResponseEntity<List<ServiceSpendingAggregation>> getTopServices(
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "10") int limit) {

        String actualBuyerId = buyerId != null ? buyerId : properties.getDefaultBuyerId();
        if (actualBuyerId == null) {
            actualBuyerId = "default";
        }

        OffsetDateTime fromDate = from != null ? OffsetDateTime.parse(from) : OffsetDateTime.now().minusDays(30);
        OffsetDateTime toDate = to != null ? OffsetDateTime.parse(to) : OffsetDateTime.now();

        List<ServiceSpendingAggregation> services = aggregationService.getTopServices(
            actualBuyerId, fromDate, toDate, limit
        );

        return ResponseEntity.ok(services);
    }

    /**
     * Get recent transactions.
     */
    @GetMapping("/transactions/recent")
    public ResponseEntity<List<X402SpendingEvent>> getRecentTransactions(
            @RequestParam(required = false) String buyerId,
            @RequestParam(defaultValue = "10") int limit) {

        String actualBuyerId = buyerId != null ? buyerId : properties.getDefaultBuyerId();
        if (actualBuyerId == null) {
            actualBuyerId = "default";
        }

        List<X402SpendingEvent> events = eventService.findRecent(actualBuyerId);
        return ResponseEntity.ok(events.stream().limit(limit).toList());
    }
}
