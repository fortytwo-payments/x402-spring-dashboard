package io.x402.dashboard.service.dto;

/**
 * DTO for endpoint aggregation results.
 */
public class EndpointAggregation {
    private String endpoint;
    private Long count;
    private Long amountAtomic;

    public EndpointAggregation() {
    }

    public EndpointAggregation(String endpoint, Long count, Long amountAtomic) {
        this.endpoint = endpoint;
        this.count = count;
        this.amountAtomic = amountAtomic;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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
