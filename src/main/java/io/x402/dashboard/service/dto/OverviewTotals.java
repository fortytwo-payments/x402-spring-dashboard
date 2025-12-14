package io.x402.dashboard.service.dto;

/**
 * DTO for overview totals.
 */
public class OverviewTotals {
    private Long totalCount;
    private Long totalAmount;
    private Long successCount;
    private Long successAmount;

    public OverviewTotals() {
    }

    public OverviewTotals(Long totalCount, Long totalAmount, Long successCount, Long successAmount) {
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.successCount = successCount;
        this.successAmount = successAmount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getSuccessAmount() {
        return successAmount;
    }

    public void setSuccessAmount(Long successAmount) {
        this.successAmount = successAmount;
    }

    public Double getSuccessRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (successCount != null ? successCount.doubleValue() : 0.0) / totalCount.doubleValue() * 100;
    }
}
