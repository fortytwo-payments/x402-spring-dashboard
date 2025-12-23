package io.x402.dashboard.seller.service;

import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.repository.X402UsageEventRepository;
import io.x402.dashboard.seller.service.dto.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for aggregating X402UsageEvent data for dashboard.
 */
@Service
public class X402UsageAggregationService {

    private final X402UsageEventRepository repository;

    public X402UsageAggregationService(X402UsageEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Aggregate by agent.
     */
    public List<AgentAggregation> aggregateByAgent(
            String tenantId,
            OffsetDateTime from,
            OffsetDateTime to,
            X402UsageStatus status) {
        List<Object[]> rows = repository.aggregateByAgent(tenantId, from, to, status);
        return rows.stream()
                .map(r -> new AgentAggregation(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Aggregate by endpoint.
     */
    public List<EndpointAggregation> aggregateByEndpoint(
            String tenantId,
            OffsetDateTime from,
            OffsetDateTime to,
            X402UsageStatus status) {
        List<Object[]> rows = repository.aggregateByEndpoint(tenantId, from, to, status);
        return rows.stream()
                .map(r -> new EndpointAggregation(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Aggregate by status.
     */
    public List<StatusAggregation> aggregateByStatus(
            String tenantId,
            OffsetDateTime from,
            OffsetDateTime to) {
        List<Object[]> rows = repository.aggregateByStatus(tenantId, from, to);
        return rows.stream()
                .map(r -> new StatusAggregation(
                        (X402UsageStatus) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Aggregate by date (daily).
     */
    public List<DateAggregation> aggregateByDate(
            String tenantId,
            OffsetDateTime from,
            OffsetDateTime to,
            X402UsageStatus status) {
        String statusStr = status != null ? status.name() : null;
        List<Object[]> rows = repository.aggregateByDate(tenantId, from, to, statusStr);
        return rows.stream()
                .map(r -> new DateAggregation(
                        r[0] != null ? r[0].toString() : null,
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get overview totals.
     */
    public OverviewTotals getOverviewTotals(
            String tenantId,
            OffsetDateTime from,
            OffsetDateTime to) {
        Object[] result = repository.getTotals(tenantId, from, to);
        if (result == null || result.length == 0) {
            return new OverviewTotals(0L, 0L, 0L, 0L);
        }
        // The result is a nested array - outer array contains rows, inner array contains columns
        Object[] row = (Object[]) result[0];
        if (row == null || row.length < 4) {
            return new OverviewTotals(0L, 0L, 0L, 0L);
        }
        return new OverviewTotals(
                row[0] != null ? ((Number) row[0]).longValue() : 0L,
                row[1] != null ? ((Number) row[1]).longValue() : 0L,
                row[2] != null ? ((Number) row[2]).longValue() : 0L,
                row[3] != null ? ((Number) row[3]).longValue() : 0L
        );
    }
}
