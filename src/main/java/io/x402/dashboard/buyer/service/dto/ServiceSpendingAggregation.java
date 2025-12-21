package io.x402.dashboard.buyer.service.dto;

import io.x402.dashboard.buyer.domain.ServiceCategory;

/**
 * DTO for service-level spending aggregation.
 */
public class ServiceSpendingAggregation {

    private String serviceId;
    private String serviceName;
    private ServiceCategory category;
    private Long requestCount;
    private Long totalSpentAtomic;
    private Long avgCostAtomic;
    private Double percentOfTotal;

    public ServiceSpendingAggregation() {
    }

    public ServiceSpendingAggregation(String serviceId, String serviceName, ServiceCategory category,
                                      Long requestCount, Long totalSpentAtomic,
                                      Long avgCostAtomic, Double percentOfTotal) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.category = category;
        this.requestCount = requestCount;
        this.totalSpentAtomic = totalSpentAtomic;
        this.avgCostAtomic = avgCostAtomic;
        this.percentOfTotal = percentOfTotal;
    }

    // Getters and Setters

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public Long getTotalSpentAtomic() {
        return totalSpentAtomic;
    }

    public void setTotalSpentAtomic(Long totalSpentAtomic) {
        this.totalSpentAtomic = totalSpentAtomic;
    }

    public Long getAvgCostAtomic() {
        return avgCostAtomic;
    }

    public void setAvgCostAtomic(Long avgCostAtomic) {
        this.avgCostAtomic = avgCostAtomic;
    }

    public Double getPercentOfTotal() {
        return percentOfTotal;
    }

    public void setPercentOfTotal(Double percentOfTotal) {
        this.percentOfTotal = percentOfTotal;
    }
}
