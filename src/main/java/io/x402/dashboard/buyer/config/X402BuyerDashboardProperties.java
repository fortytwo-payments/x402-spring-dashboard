package io.x402.dashboard.buyer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for X402 Buyer Dashboard.
 */
@Component
@ConfigurationProperties(prefix = "x402.buyer.dashboard")
public class X402BuyerDashboardProperties {

    /**
     * Enable or disable buyer dashboard.
     */
    private boolean enabled = false;

    /**
     * Dashboard UI path.
     */
    private String path = "/x402-buyer-dashboard";

    /**
     * REST API path prefix.
     */
    private String apiPath = "/x402-buyer-dashboard/api";

    /**
     * Default buyer ID (for single-buyer scenarios).
     * If not set, buyerId must be provided in logging calls.
     */
    private String defaultBuyerId;

    /**
     * Default buyer name.
     */
    private String defaultBuyerName;

    /**
     * Enable auto-logging via HTTP client interceptor.
     */
    private boolean enableAutoLogging = false;

    /**
     * Service ID mapping for auto-logging.
     * Format: "host:serviceId" (e.g., "api.openai.com:openai-api")
     */
    private String serviceIdMapping;

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getDefaultBuyerId() {
        return defaultBuyerId;
    }

    public void setDefaultBuyerId(String defaultBuyerId) {
        this.defaultBuyerId = defaultBuyerId;
    }

    public String getDefaultBuyerName() {
        return defaultBuyerName;
    }

    public void setDefaultBuyerName(String defaultBuyerName) {
        this.defaultBuyerName = defaultBuyerName;
    }

    public boolean isEnableAutoLogging() {
        return enableAutoLogging;
    }

    public void setEnableAutoLogging(boolean enableAutoLogging) {
        this.enableAutoLogging = enableAutoLogging;
    }

    public String getServiceIdMapping() {
        return serviceIdMapping;
    }

    public void setServiceIdMapping(String serviceIdMapping) {
        this.serviceIdMapping = serviceIdMapping;
    }
}
