package io.x402.dashboard.service.dto;

/**
 * DTO for date-based aggregation results.
 */
public class DateAggregation {
    private String date;
    private Long count;
    private Long amountAtomic;

    public DateAggregation() {
    }

    public DateAggregation(String date, Long count, Long amountAtomic) {
        this.date = date;
        this.count = count;
        this.amountAtomic = amountAtomic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
