package io.x402.dashboard.buyer.service.dto;

import java.time.OffsetDateTime;

/**
 * DTO for spending overview statistics.
 */
public class SpendingOverview {

    private Long totalSpentAtomic;
    private Long totalRequests;
    private Long successCount;
    private Double successRate;
    private Long avgCostAtomic;
    private OffsetDateTime from;
    private OffsetDateTime to;

    public SpendingOverview() {
    }

    public SpendingOverview(Long totalSpentAtomic, Long totalRequests, Long successCount,
                            Double successRate, Long avgCostAtomic,
                            OffsetDateTime from, OffsetDateTime to) {
        this.totalSpentAtomic = totalSpentAtomic;
        this.totalRequests = totalRequests;
        this.successCount = successCount;
        this.successRate = successRate;
        this.avgCostAtomic = avgCostAtomic;
        this.from = from;
        this.to = to;
    }

    // Getters and Setters

    public Long getTotalSpentAtomic() {
        return totalSpentAtomic;
    }

    public void setTotalSpentAtomic(Long totalSpentAtomic) {
        this.totalSpentAtomic = totalSpentAtomic;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Long getAvgCostAtomic() {
        return avgCostAtomic;
    }

    public void setAvgCostAtomic(Long avgCostAtomic) {
        this.avgCostAtomic = avgCostAtomic;
    }

    public OffsetDateTime getFrom() {
        return from;
    }

    public void setFrom(OffsetDateTime from) {
        this.from = from;
    }

    public OffsetDateTime getTo() {
        return to;
    }

    public void setTo(OffsetDateTime to) {
        this.to = to;
    }
}
