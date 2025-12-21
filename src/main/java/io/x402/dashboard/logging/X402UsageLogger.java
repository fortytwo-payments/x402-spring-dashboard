package io.x402.dashboard.logging;

import io.x402.dashboard.domain.AgentType;
import io.x402.dashboard.domain.X402UsageEvent;
import io.x402.dashboard.domain.X402UsageStatus;
import io.x402.dashboard.service.X402UsageEventService;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Logger for x402 usage events.
 * This class can be injected into application code to log x402 payment events.
 *
 * Example usage:
 * <pre>
 * &#64;Autowired
 * private X402UsageLogger usageLogger;
 *
 * public void handlePayment() {
 *     usageLogger.log(
 *         null,                    // tenantId
 *         "agent-123",             // agentId
 *         AgentType.CLAUDE,        // agentType
 *         "POST",                  // method
 *         "/api/resource",         // endpoint
 *         "billing-key",           // billingKey
 *         "eip155:84532",          // network (CAIP-2 format: Base Sepolia)
 *         "USDC",                  // asset
 *         1000000L,                // amountAtomic (1 USDC)
 *         "0x123...",              // txHash
 *         X402UsageStatus.SUCCESS, // status
 *         "192.168.1.1",           // clientIp
 *         "Mozilla/5.0...",        // userAgent
 *         150L,                    // latencyMs
 *         OffsetDateTime.now()     // settledAt
 *     );
 * }
 * </pre>
 */
@Component
public class X402UsageLogger {

    private final X402UsageEventService eventService;

    public X402UsageLogger(X402UsageEventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Log a complete usage event.
     */
    public X402UsageEvent log(
            String tenantId,
            String agentId,
            AgentType agentType,
            String method,
            String endpoint,
            String billingKey,
            String network,
            String asset,
            Long amountAtomic,
            String txHash,
            X402UsageStatus status,
            String clientIp,
            String userAgent,
            Long latencyMs,
            OffsetDateTime createdAt,
            OffsetDateTime settledAt
    ) {
        X402UsageEvent event = new X402UsageEvent();
        event.setTenantId(tenantId);
        event.setAgentId(agentId);
        event.setAgentType(agentType);
        event.setMethod(method);
        event.setEndpoint(endpoint);
        event.setBillingKey(billingKey);
        event.setNetwork(network);
        event.setAsset(asset);
        event.setAmountAtomic(amountAtomic);
        event.setTxHash(txHash);
        event.setStatus(status);
        event.setClientIp(clientIp);
        event.setUserAgent(userAgent);
        event.setLatencyMs(latencyMs);
        event.setCreatedAt(createdAt != null ? createdAt : OffsetDateTime.now());
        event.setSettledAt(settledAt);
        return eventService.save(event);
    }

    /**
     * Log a simple usage event (minimal parameters).
     */
    public X402UsageEvent logSimple(
            String method,
            String endpoint,
            X402UsageStatus status,
            Long latencyMs
    ) {
        return log(null, null, null, method, endpoint, null, null, null, null, null, status, null, null, latencyMs, null, null);
    }

    /**
     * Log a payment success event.
     */
    public X402UsageEvent logSuccess(
            String agentId,
            String method,
            String endpoint,
            String network,
            String asset,
            Long amountAtomic,
            String txHash,
            Long latencyMs
    ) {
        return log(null, agentId, null, method, endpoint, null, network, asset, amountAtomic, txHash,
                X402UsageStatus.SUCCESS, null, null, latencyMs, null, OffsetDateTime.now());
    }

    /**
     * Log a 402 payment required event.
     */
    public X402UsageEvent logPaymentRequired(
            String agentId,
            String method,
            String endpoint,
            String network,
            String asset,
            Long amountAtomic,
            Long latencyMs
    ) {
        return log(null, agentId, null, method, endpoint, null, network, asset, amountAtomic, null,
                X402UsageStatus.PAYMENT_REQUIRED, null, null, latencyMs, null, null);
    }

    /**
     * Log a verification failure event.
     */
    public X402UsageEvent logVerifyFailed(
            String agentId,
            String method,
            String endpoint,
            Long latencyMs
    ) {
        return log(null, agentId, null, method, endpoint, null, null, null, null, null,
                X402UsageStatus.VERIFY_FAILED, null, null, latencyMs, null, null);
    }

    /**
     * Log a settlement failure event.
     */
    public X402UsageEvent logSettleFailed(
            String agentId,
            String method,
            String endpoint,
            String txHash,
            Long latencyMs
    ) {
        return log(null, agentId, null, method, endpoint, null, null, null, null, txHash,
                X402UsageStatus.SETTLE_FAILED, null, null, latencyMs, null, null);
    }

    /**
     * Create a builder for more flexible event logging.
     */
    public X402UsageEventBuilder builder() {
        return new X402UsageEventBuilder(this);
    }

    /**
     * Builder for constructing X402UsageEvent objects.
     */
    public static class X402UsageEventBuilder {
        private final X402UsageLogger logger;
        private String tenantId;
        private String agentId;
        private AgentType agentType;
        private String method;
        private String endpoint;
        private String billingKey;
        private String network;
        private String asset;
        private Long amountAtomic;
        private String txHash;
        private X402UsageStatus status;
        private String clientIp;
        private String userAgent;
        private Long latencyMs;
        private OffsetDateTime createdAt;
        private OffsetDateTime settledAt;

        public X402UsageEventBuilder(X402UsageLogger logger) {
            this.logger = logger;
        }

        public X402UsageEventBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public X402UsageEventBuilder agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }

        public X402UsageEventBuilder agentType(AgentType agentType) {
            this.agentType = agentType;
            return this;
        }

        public X402UsageEventBuilder method(String method) {
            this.method = method;
            return this;
        }

        public X402UsageEventBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public X402UsageEventBuilder billingKey(String billingKey) {
            this.billingKey = billingKey;
            return this;
        }

        public X402UsageEventBuilder network(String network) {
            this.network = network;
            return this;
        }

        public X402UsageEventBuilder asset(String asset) {
            this.asset = asset;
            return this;
        }

        public X402UsageEventBuilder amountAtomic(Long amountAtomic) {
            this.amountAtomic = amountAtomic;
            return this;
        }

        public X402UsageEventBuilder txHash(String txHash) {
            this.txHash = txHash;
            return this;
        }

        public X402UsageEventBuilder status(X402UsageStatus status) {
            this.status = status;
            return this;
        }

        public X402UsageEventBuilder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public X402UsageEventBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public X402UsageEventBuilder latencyMs(Long latencyMs) {
            this.latencyMs = latencyMs;
            return this;
        }

        public X402UsageEventBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public X402UsageEventBuilder settledAt(OffsetDateTime settledAt) {
            this.settledAt = settledAt;
            return this;
        }

        public X402UsageEvent log() {
            return logger.log(tenantId, agentId, agentType, method, endpoint, billingKey,
                    network, asset, amountAtomic, txHash, status, clientIp, userAgent, latencyMs, createdAt, settledAt);
        }
    }
}
