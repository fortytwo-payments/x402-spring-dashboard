package io.x402.dashboard.buyer.service;

import io.x402.dashboard.buyer.domain.ServiceCategory;
import io.x402.dashboard.buyer.domain.SpendingStatus;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import io.x402.dashboard.buyer.repository.X402SpendingEventRepository;
import io.x402.dashboard.buyer.service.dto.ServiceSpendingAggregation;
import io.x402.dashboard.buyer.service.dto.SpendingOverview;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for aggregating and analyzing spending data.
 */
@Service
@Transactional(readOnly = true)
public class X402SpendingAggregationService {

    private final X402SpendingEventRepository repository;

    public X402SpendingAggregationService(X402SpendingEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Get spending overview for a buyer within time range.
     */
    public SpendingOverview getOverview(String buyerId, OffsetDateTime from, OffsetDateTime to) {
        // Total spent (SUCCESS only)
        Long totalSpent = repository.sumAmountByBuyerAndTimeRange(buyerId, from, to);
        if (totalSpent == null) {
            totalSpent = 0L;
        }

        // Total requests
        Long totalRequests = repository.countByBuyerIdAndCreatedAtBetween(buyerId, from, to);
        if (totalRequests == null) {
            totalRequests = 0L;
        }

        // Success count
        Long successCount = repository.countByBuyerIdAndStatusAndCreatedAtBetween(
            buyerId, SpendingStatus.SUCCESS, from, to
        );
        if (successCount == null) {
            successCount = 0L;
        }

        // Success rate
        Double successRate = 0.0;
        if (totalRequests > 0) {
            successRate = (successCount.doubleValue() / totalRequests.doubleValue()) * 100.0;
        }

        // Average cost
        Long avgCost = 0L;
        if (successCount > 0 && totalSpent > 0) {
            avgCost = totalSpent / successCount;
        }

        return new SpendingOverview(
            totalSpent,
            totalRequests,
            successCount,
            successRate,
            avgCost,
            from,
            to
        );
    }

    /**
     * Get top services by spending.
     */
    public List<ServiceSpendingAggregation> getTopServices(
            String buyerId,
            OffsetDateTime from,
            OffsetDateTime to,
            int limit) {

        // Get all successful spending events
        List<X402SpendingEvent> events = repository.findByBuyerIdAndStatusAndCreatedAtBetween(
            buyerId, SpendingStatus.SUCCESS, from, to
        );

        // Calculate total spending
        long totalSpent = events.stream()
            .mapToLong(e -> e.getAmountAtomic() != null ? e.getAmountAtomic() : 0L)
            .sum();

        // Group by service ID
        Map<String, List<X402SpendingEvent>> grouped = events.stream()
            .filter(e -> e.getServiceId() != null)
            .collect(Collectors.groupingBy(X402SpendingEvent::getServiceId));

        // Aggregate and sort
        return grouped.entrySet().stream()
            .map(entry -> {
                String serviceId = entry.getKey();
                List<X402SpendingEvent> serviceEvents = entry.getValue();

                long serviceTotal = serviceEvents.stream()
                    .mapToLong(e -> e.getAmountAtomic() != null ? e.getAmountAtomic() : 0L)
                    .sum();

                long requestCount = serviceEvents.size();
                long avgCost = requestCount > 0 ? serviceTotal / requestCount : 0L;
                double percentOfTotal = totalSpent > 0 ? (serviceTotal * 100.0) / totalSpent : 0.0;

                // Get service name and category from first event
                String serviceName = serviceEvents.get(0).getServiceName();
                if (serviceName == null) {
                    serviceName = serviceId;
                }
                ServiceCategory category = serviceEvents.get(0).getCategory();

                return new ServiceSpendingAggregation(
                    serviceId,
                    serviceName,
                    category,
                    requestCount,
                    serviceTotal,
                    avgCost,
                    percentOfTotal
                );
            })
            .sorted(Comparator.comparing(ServiceSpendingAggregation::getTotalSpentAtomic).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Get spending by category.
     */
    public Map<ServiceCategory, Long> getSpendingByCategory(
            String buyerId,
            OffsetDateTime from,
            OffsetDateTime to) {

        List<X402SpendingEvent> events = repository.findByBuyerIdAndStatusAndCreatedAtBetween(
            buyerId, SpendingStatus.SUCCESS, from, to
        );

        return events.stream()
            .filter(e -> e.getCategory() != null && e.getAmountAtomic() != null)
            .collect(Collectors.groupingBy(
                X402SpendingEvent::getCategory,
                Collectors.summingLong(X402SpendingEvent::getAmountAtomic)
            ));
    }
}
