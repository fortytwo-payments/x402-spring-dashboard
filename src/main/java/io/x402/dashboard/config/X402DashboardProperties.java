package io.x402.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for x402 Dashboard.
 */
@ConfigurationProperties(prefix = "x402.dashboard")
public class X402DashboardProperties {

    /**
     * Dashboard root path (e.g., "/x402-dashboard")
     */
    private String path = "/x402-dashboard";

    /**
     * Dashboard REST API prefix (e.g., "/x402-dashboard/api")
     */
    private String apiPath = "/x402-dashboard/api";

    /**
     * Enable server-side auto logging interceptor (for incoming requests)
     */
    private boolean enableAutoLogging = false;

    /**
     * Enable client-side auto logging interceptor (for outgoing RestTemplate/RestClient requests)
     */
    private boolean enableClientAutoLogging = false;

    /**
     * H2 in-memory mode
     */
    private boolean inMemory = true;

    /**
     * H2 file DB path (when inMemory=false)
     */
    private String filePath = "./x402-dashboard-db";

    /**
     * Default tenant ID for single-tenant mode
     */
    private String defaultTenantId = null;

    /**
     * Enable security (Basic Auth)
     */
    private boolean securityEnabled = false;

    /**
     * Security username
     */
    private String securityUsername = "admin";

    /**
     * Security password
     */
    private String securityPassword = "admin";

    // Getters and Setters
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

    public boolean isEnableAutoLogging() {
        return enableAutoLogging;
    }

    public void setEnableAutoLogging(boolean enableAutoLogging) {
        this.enableAutoLogging = enableAutoLogging;
    }

    public boolean isEnableClientAutoLogging() {
        return enableClientAutoLogging;
    }

    public void setEnableClientAutoLogging(boolean enableClientAutoLogging) {
        this.enableClientAutoLogging = enableClientAutoLogging;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDefaultTenantId() {
        return defaultTenantId;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public String getSecurityUsername() {
        return securityUsername;
    }

    public void setSecurityUsername(String securityUsername) {
        this.securityUsername = securityUsername;
    }

    public String getSecurityPassword() {
        return securityPassword;
    }

    public void setSecurityPassword(String securityPassword) {
        this.securityPassword = securityPassword;
    }
}
