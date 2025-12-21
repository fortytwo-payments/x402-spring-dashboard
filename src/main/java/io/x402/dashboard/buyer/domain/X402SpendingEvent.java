package io.x402.dashboard.buyer.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entity representing a spending event from the buyer's perspective.
 * Tracks outbound payments made by AI agents/clients to external services.
 */
@Entity
@Table(name = "x402_spending_event", indexes = {
    @Index(name = "idx_spending_buyer_created", columnList = "buyer_id, created_at"),
    @Index(name = "idx_spending_service_created", columnList = "service_id, created_at"),
    @Index(name = "idx_spending_category", columnList = "category"),
    @Index(name = "idx_spending_status", columnList = "status"),
    @Index(name = "idx_spending_budget", columnList = "budget_id")
})
public class X402SpendingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================
    // WHO: Who is spending
    // ========================================

    /**
     * The buyer/agent identifier doing the spending.
     */
    @Column(name = "buyer_id")
    private String buyerId;

    /**
     * Friendly name of the buyer/agent.
     */
    @Column(name = "buyer_name")
    private String buyerName;

    // ========================================
    // WHERE: Where are we spending
    // ========================================

    /**
     * Target service identifier (unique ID).
     */
    @Column(name = "service_id")
    private String serviceId;

    /**
     * Service name (e.g., "OpenAI API", "Weather Service").
     */
    @Column(name = "service_name")
    private String serviceName;

    /**
     * Base URL of the service.
     */
    @Column(name = "service_url")
    private String serviceUrl;

    /**
     * Specific endpoint called (e.g., "/chat/completions").
     */
    @Column(name = "endpoint")
    private String endpoint;

    // ========================================
    // WHAT: What category
    // ========================================

    /**
     * Category of service for cost analysis.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    private ServiceCategory category;

    // ========================================
    // HOW MUCH: Payment details
    // ========================================

    /**
     * Blockchain network in CAIP-2 format (e.g., eip155:84532 for Base Sepolia).
     */
    @Column(name = "network")
    private String network;

    /**
     * Asset type (e.g., USDC, ETH).
     */
    @Column(name = "asset")
    private String asset;

    /**
     * Amount in atomic units (e.g., wei for ETH, smallest unit for USDC).
     */
    @Column(name = "amount_atomic")
    private Long amountAtomic;

    // ========================================
    // WHEN: Timestamps
    // ========================================

    /**
     * When the request was initiated.
     */
    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    /**
     * When the payment was settled on blockchain.
     */
    @Column(name = "settled_at")
    private OffsetDateTime settledAt;

    /**
     * Event creation timestamp (auto-set).
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // ========================================
    // STATUS: Payment status
    // ========================================

    /**
     * Status of the spending event.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private SpendingStatus status;

    // ========================================
    // TRANSACTION INFO
    // ========================================

    /**
     * Blockchain transaction hash (if settled).
     */
    @Column(name = "tx_hash")
    private String txHash;

    /**
     * Internal payment identifier.
     */
    @Column(name = "payment_id")
    private String paymentId;

    // ========================================
    // BUDGET TRACKING
    // ========================================

    /**
     * Associated budget ID (for budget tracking).
     */
    @Column(name = "budget_id")
    private String budgetId;

    /**
     * Project or campaign identifier.
     */
    @Column(name = "project_id")
    private String projectId;

    // ========================================
    // METADATA
    // ========================================

    /**
     * Request latency in milliseconds.
     */
    @Column(name = "latency_ms")
    private Long latencyMs;

    /**
     * Error message if payment failed.
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /**
     * Additional metadata as JSON string.
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * HTTP method used (GET, POST, etc.).
     */
    @Column(name = "method", length = 10)
    private String method;

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

    // ========================================
    // Lifecycle Callbacks
    // ========================================

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (requestedAt == null) {
            requestedAt = OffsetDateTime.now();
        }
    }

    // ========================================
    // Constructors
    // ========================================

    public X402SpendingEvent() {
    }

    // ========================================
    // Getters and Setters
    // ========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

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

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
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

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(OffsetDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public OffsetDateTime getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(OffsetDateTime settledAt) {
        this.settledAt = settledAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SpendingStatus getStatus() {
        return status;
    }

    public void setStatus(SpendingStatus status) {
        this.status = status;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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
}
