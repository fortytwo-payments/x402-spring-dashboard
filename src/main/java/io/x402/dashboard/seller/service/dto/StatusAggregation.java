package io.x402.dashboard.seller.service.dto;

import io.x402.dashboard.seller.domain.X402UsageStatus;

/**
 * DTO for status aggregation results.
 */
public class StatusAggregation {
    private X402UsageStatus status;
    private Long count;
    private Long amountAtomic;

    public StatusAggregation() {
    }

    public StatusAggregation(X402UsageStatus status, Long count, Long amountAtomic) {
        this.status = status;
        this.count = count;
        this.amountAtomic = amountAtomic;
    }

    public X402UsageStatus getStatus() {
        return status;
    }

    public void setStatus(X402UsageStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getAmountAtomic() {
        return amountAtomic;
    }

    public void setAmountAtomic(Long amountAtomic) {
        this.amountAtomic = amountAtomic;
    }
}
