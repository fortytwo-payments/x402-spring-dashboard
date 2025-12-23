package io.x402.dashboard.seller.service.dto;

/**
 * DTO for agent aggregation results.
 */
public class AgentAggregation {
    private String agentId;
    private Long count;
    private Long amountAtomic;

    public AgentAggregation() {
    }

    public AgentAggregation(String agentId, Long count, Long amountAtomic) {
        this.agentId = agentId;
        this.count = count;
        this.amountAtomic = amountAtomic;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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
