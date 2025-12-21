package io.x402.dashboard.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entity representing an x402 usage event.
 */
@Entity
@Table(name = "x402_usage_event", indexes = {
    @Index(name = "idx_tenant_created", columnList = "tenant_id, created_at"),
    @Index(name = "idx_agent_created", columnList = "agent_id, created_at"),
    @Index(name = "idx_endpoint_created", columnList = "endpoint, created_at"),
    @Index(name = "idx_status", columnList = "status")
})
public class X402UsageEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tenant/service identifier for multi-tenant support.
     */
    @Column(name = "tenant_id")
    private String tenantId;

    /**
     * AI agent identifier.
     */
    @Column(name = "agent_id")
    private String agentId;

    /**
     * Type of AI agent.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "agent_type", length = 20)
    private AgentType agentType;

    /**
     * HTTP method (GET, POST, etc.)
     */
    @Column(name = "method", length = 10)
    private String method;

    /**
     * Request endpoint/URI.
     */
    @Column(name = "endpoint")
    private String endpoint;

    /**
     * Billing key for logical billing unit.
     */
    @Column(name = "billing_key")
    private String billingKey;

    /**
     * Blockchain network in CAIP-2 format (e.g., eip155:84532 for Base Sepolia, eip155:1 for Ethereum Mainnet).
     */
    @Column(name = "network")
    private String network;

    /**
     * Asset type (e.g., USDC, ETH).
     */
    @Column(name = "asset")
    private String asset;

    /**
     * Amount in atomic units (e.g., wei, smallest unit).
     */
    @Column(name = "amount_atomic")
    private Long amountAtomic;

    /**
     * Transaction hash (on success).
     */
    @Column(name = "tx_hash")
    private String txHash;

    /**
     * Event status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private X402UsageStatus status;

    /**
     * Client IP address.
     */
    @Column(name = "client_ip")
    private String clientIp;

    /**
     * User-Agent header value.
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Request latency in milliseconds.
     */
    @Column(name = "latency_ms")
    private Long latencyMs;

    /**
     * Event creation timestamp.
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Settlement timestamp.
     */
    @Column(name = "settled_at")
    private OffsetDateTime settledAt;

    /**
     * Additional metadata as JSON string.
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // Constructors
    public X402UsageEvent() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBillingKey() {
        return billingKey;
    }

    public void setBillingKey(String billingKey) {
        this.billingKey = billingKey;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Long getAmountAtomic() {
        return amountAtomic;
    }

    public void setAmountAtomic(Long amountAtomic) {
        this.amountAtomic = amountAtomic;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public X402UsageStatus getStatus() {
        return status;
    }

    public void setStatus(X402UsageStatus status) {
        this.status = status;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(OffsetDateTime settledAt) {
        this.settledAt = settledAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
