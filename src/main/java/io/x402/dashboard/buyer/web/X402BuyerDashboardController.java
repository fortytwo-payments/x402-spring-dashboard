package io.x402.dashboard.buyer.web;

import io.x402.dashboard.buyer.config.X402BuyerDashboardProperties;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import io.x402.dashboard.buyer.service.X402SpendingAggregationService;
import io.x402.dashboard.buyer.service.X402SpendingEventService;
import io.x402.dashboard.buyer.service.dto.ServiceSpendingAggregation;
import io.x402.dashboard.buyer.service.dto.SpendingOverview;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Web Controller for Buyer Dashboard UI.
 */
@Controller
@RequestMapping("${x402.buyer.dashboard.path:/x402-buyer-dashboard}")
@ConditionalOnProperty(name = "x402.buyer.dashboard.enabled", havingValue = "true")
public class X402BuyerDashboardController {

    private final X402SpendingAggregationService aggregationService;
    private final X402SpendingEventService eventService;
    private final X402BuyerDashboardProperties properties;

    public X402BuyerDashboardController(
            X402SpendingAggregationService aggregationService,
            X402SpendingEventService eventService,
            X402BuyerDashboardProperties properties) {
        this.aggregationService = aggregationService;
        this.eventService = eventService;
        this.properties = properties;
    }

    /**
     * Buyer Dashboard Overview Page.
     */
    @GetMapping
    public String overview(
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

        String actualBuyerId = buyerId != null ? buyerId : properties.getDefaultBuyerId();
        if (actualBuyerId == null) {
            actualBuyerId = "default";
        }

        OffsetDateTime fromDate = (from != null && !from.isBlank()) ?
            LocalDateTime.parse(from).atZone(ZoneId.systemDefault()).toOffsetDateTime() :
            OffsetDateTime.now().minusDays(30);
        OffsetDateTime toDate = (to != null && !to.isBlank()) ?
            LocalDateTime.parse(to).atZone(ZoneId.systemDefault()).toOffsetDateTime() :
            OffsetDateTime.now();

        // Get overview statistics
        SpendingOverview overview = aggregationService.getOverview(actualBuyerId, fromDate, toDate);

        // Get top 5 services
        List<ServiceSpendingAggregation> topServices = aggregationService.getTopServices(
            actualBuyerId, fromDate, toDate, 5
        );

        // Get recent 10 transactions
        List<X402SpendingEvent> recentTransactions = eventService.findRecent(actualBuyerId);

        // Add to model
        model.addAttribute("overview", overview);
        model.addAttribute("topServices", topServices);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("buyerId", actualBuyerId);
        model.addAttribute("from", fromDate);
        model.addAttribute("to", toDate);

        return "x402-buyer-dashboard/overview";
    }

    /**
     * Services Page - Detailed service analysis.
     */
    @GetMapping("/services")
    public String services(
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

        String actualBuyerId = buyerId != null ? buyerId : properties.getDefaultBuyerId();
        if (actualBuyerId == null) {
            actualBuyerId = "default";
        }

        OffsetDateTime fromDate = (from != null && !from.isBlank()) ?
            LocalDateTime.parse(from).atZone(ZoneId.systemDefault()).toOffsetDateTime() :
            OffsetDateTime.now().minusDays(30);
        OffsetDateTime toDate = (to != null && !to.isBlank()) ?
            LocalDateTime.parse(to).atZone(ZoneId.systemDefault()).toOffsetDateTime() :
            OffsetDateTime.now();

        // Get all services (no limit)
        List<ServiceSpendingAggregation> services = aggregationService.getTopServices(
            actualBuyerId, fromDate, toDate, 100
        );

        // Get overview for totals
        SpendingOverview overview = aggregationService.getOverview(actualBuyerId, fromDate, toDate);

        model.addAttribute("services", services);
        model.addAttribute("overview", overview);
        model.addAttribute("buyerId", actualBuyerId);
        model.addAttribute("from", fromDate);
        model.addAttribute("to", toDate);

        return "x402-buyer-dashboard/services";
    }

    /**
     * Transactions Page - Full transaction list with filtering.
     */
    @GetMapping("/transactions")
    public String transactions(
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        String actualBuyerId = buyerId != null ? buyerId : properties.getDefaultBuyerId();
        if (actualBuyerId == null) {
            actualBuyerId = "default";
        }

        OffsetDateTime fromDate = (from != null && !from.isBlank()) ?
            LocalDateTime.parse(from).atZone(ZoneId.systemDefault()).toOffsetDateTime() :
            OffsetDateTime.now().minusDays(30);
        OffsetDateTime toDate = (to != null && !to.isBlank()) ?
            LocalDateTime.parse(to).atZone(ZoneId.systemDefault()).toOffsetDateTime() :
            OffsetDateTime.now();

        // Normalize empty strings to null
        String actualServiceId = (serviceId != null && !serviceId.isBlank()) ? serviceId : null;

        // Parse status
        io.x402.dashboard.buyer.domain.SpendingStatus spendingStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                spendingStatus = io.x402.dashboard.buyer.domain.SpendingStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }

        // Get paginated transactions
        org.springframework.data.domain.Page<X402SpendingEvent> transactionsPage =
            eventService.findWithFilters(
                actualBuyerId,
                actualServiceId,
                spendingStatus,
                fromDate,
                toDate,
                org.springframework.data.domain.PageRequest.of(page, size,
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
            );

        model.addAttribute("transactions", transactionsPage);
        model.addAttribute("buyerId", actualBuyerId);
        model.addAttribute("serviceId", serviceId);
        model.addAttribute("status", status);
        model.addAttribute("from", fromDate);
        model.addAttribute("to", toDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionsPage.getTotalPages());

        return "x402-buyer-dashboard/transactions";
    }
}
