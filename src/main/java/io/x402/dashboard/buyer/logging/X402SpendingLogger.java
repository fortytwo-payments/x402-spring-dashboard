package io.x402.dashboard.buyer.logging;

import io.x402.dashboard.buyer.domain.ServiceCategory;
import io.x402.dashboard.buyer.domain.SpendingStatus;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import io.x402.dashboard.buyer.repository.X402SpendingEventRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Logger for x402 spending events from buyer's perspective.
 * This class can be injected into application code to log outbound x402 payment events.
 *
 * Example usage:
 * <pre>
 * &#64;Autowired
 * private X402SpendingLogger spendingLogger;
 *
 * public void callExternalService() {
 *     spendingLogger.logSuccess(
 *         "my-agent-001",           // buyerId
 *         "openai-api",             // serviceId
 *         "OpenAI API",             // serviceName
 *         "/chat/completions",      // endpoint
 *         ServiceCategory.AI_LANGUAGE_MODEL,
 *         "eip155:84532",           // network (CAIP-2 format)
 *         "USDC",                   // asset
 *         1000000L,                 // amountAtomic (1 USDC)
 *         "0x123...",               // txHash
 *         150L                      // latencyMs
 *     );
 * }
 * </pre>
 */
@Component
public class X402SpendingLogger {

    private final X402SpendingEventRepository repository;

    public X402SpendingLogger(X402SpendingEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Log a complete spending event.
     */
    public X402SpendingEvent log(
            String buyerId,
            String buyerName,
            String serviceId,
            String serviceName,
            String serviceUrl,
            String endpoint,
            ServiceCategory category,
            String network,
            String asset,
            Long amountAtomic,
            String txHash,
            String paymentId,
            SpendingStatus status,
            String budgetId,
            String projectId,
            Long latencyMs,
            String errorMessage,
            String method,
            String clientIp,
            String userAgent,
            OffsetDateTime requestedAt,
            OffsetDateTime settledAt,
            OffsetDateTime createdAt
    ) {
        X402SpendingEvent event = new X402SpendingEvent();
        event.setBuyerId(buyerId);
        event.setBuyerName(buyerName);
        event.setServiceId(serviceId);
        event.setServiceName(serviceName);
        event.setServiceUrl(serviceUrl);
        event.setEndpoint(endpoint);
        event.setCategory(category);
        event.setNetwork(network);
        event.setAsset(asset);
        event.setAmountAtomic(amountAtomic);
        event.setTxHash(txHash);
        event.setPaymentId(paymentId);
        event.setStatus(status);
        event.setBudgetId(budgetId);
        event.setProjectId(projectId);
        event.setLatencyMs(latencyMs);
        event.setErrorMessage(errorMessage);
        event.setMethod(method);
        event.setClientIp(clientIp);
        event.setUserAgent(userAgent);
        event.setRequestedAt(requestedAt != null ? requestedAt : OffsetDateTime.now());
        event.setSettledAt(settledAt);
        event.setCreatedAt(createdAt); // Allow setting createdAt for demo data

        return repository.save(event);
    }

    /**
     * Log a successful payment.
     */
    public X402SpendingEvent logSuccess(
            String buyerId,
            String serviceId,
            String serviceName,
            String endpoint,
            ServiceCategory category,
            String network,
            String asset,
            Long amountAtomic,
            String txHash,
            Long latencyMs
    ) {
        return log(
            buyerId, null, serviceId, serviceName, null, endpoint, category,
            network, asset, amountAtomic, txHash, null,
            SpendingStatus.SUCCESS,
            null, null, latencyMs, null, null, null, null,
            null, OffsetDateTime.now(), null
        );
    }

    /**
     * Log a payment required event (402 response received).
     */
    public X402SpendingEvent logPaymentRequired(
            String buyerId,
            String serviceId,
            String serviceName,
            String endpoint,
            ServiceCategory category,
            String network,
            String asset,
            Long amountAtomic,
            Long latencyMs
    ) {
        return log(
            buyerId, null, serviceId, serviceName, null, endpoint, category,
            network, asset, amountAtomic, null, null,
            SpendingStatus.PAYMENT_REQUIRED,
            null, null, latencyMs, null, null, null, null,
            null, null, null
        );
    }

    /**
     * Log a pending payment.
     */
    public X402SpendingEvent logPending(
            String buyerId,
            String serviceId,
            String serviceName,
            String endpoint,
            ServiceCategory category,
            String network,
            String asset,
            Long amountAtomic,
            String paymentId,
            Long latencyMs
    ) {
        return log(
            buyerId, null, serviceId, serviceName, null, endpoint, category,
            network, asset, amountAtomic, null, paymentId,
            SpendingStatus.PENDING,
            null, null, latencyMs, null, null, null, null,
            null, null, null
        );
    }

    /**
     * Log a failed payment.
     */
    public X402SpendingEvent logFailed(
            String buyerId,
            String serviceId,
            String serviceName,
            String endpoint,
            String errorMessage,
            Long latencyMs
    ) {
        return log(
            buyerId, null, serviceId, serviceName, null, endpoint, null,
            null, null, null, null, null,
            SpendingStatus.FAILED,
            null, null, latencyMs, errorMessage, null, null, null,
            null, null, null
        );
    }

    /**
     * Log a rejected payment.
     */
    public X402SpendingEvent logRejected(
            String buyerId,
            String serviceId,
            String serviceName,
            String endpoint,
            String errorMessage,
            Long latencyMs
    ) {
        return log(
            buyerId, null, serviceId, serviceName, null, endpoint, null,
            null, null, null, null, null,
            SpendingStatus.REJECTED,
            null, null, latencyMs, errorMessage, null, null, null,
            null, null, null
        );
    }

    /**
     * Create a builder for more flexible event logging.
     */
    public X402SpendingEventBuilder builder() {
        return new X402SpendingEventBuilder(this);
    }

    /**
     * Builder for constructing X402SpendingEvent objects.
     */
    public static class X402SpendingEventBuilder {
        private final X402SpendingLogger logger;
        private String buyerId;
        private String buyerName;
        private String serviceId;
        private String serviceName;
        private String serviceUrl;
        private String endpoint;
        private ServiceCategory category;
        private String network;
        private String asset;
        private Long amountAtomic;
        private String txHash;
        private String paymentId;
        private SpendingStatus status;
        private String budgetId;
        private String projectId;
        private Long latencyMs;
        private String errorMessage;
        private String method;
        private String clientIp;
        private String userAgent;
        private OffsetDateTime requestedAt;
        private OffsetDateTime settledAt;
        private OffsetDateTime createdAt;

        public X402SpendingEventBuilder(X402SpendingLogger logger) {
            this.logger = logger;
        }

        public X402SpendingEventBuilder buyerId(String buyerId) {
            this.buyerId = buyerId;
            return this;
        }

        public X402SpendingEventBuilder buyerName(String buyerName) {
            this.buyerName = buyerName;
            return this;
        }

        public X402SpendingEventBuilder serviceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public X402SpendingEventBuilder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public X402SpendingEventBuilder serviceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
            return this;
        }

        public X402SpendingEventBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public X402SpendingEventBuilder category(ServiceCategory category) {
            this.category = category;
            return this;
        }

        public X402SpendingEventBuilder network(String network) {
            this.network = network;
            return this;
        }

        public X402SpendingEventBuilder asset(String asset) {
            this.asset = asset;
            return this;
        }

        public X402SpendingEventBuilder amountAtomic(Long amountAtomic) {
            this.amountAtomic = amountAtomic;
            return this;
        }

        public X402SpendingEventBuilder txHash(String txHash) {
            this.txHash = txHash;
            return this;
        }

        public X402SpendingEventBuilder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public X402SpendingEventBuilder status(SpendingStatus status) {
            this.status = status;
            return this;
        }

        public X402SpendingEventBuilder budgetId(String budgetId) {
            this.budgetId = budgetId;
            return this;
        }

        public X402SpendingEventBuilder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public X402SpendingEventBuilder latencyMs(Long latencyMs) {
            this.latencyMs = latencyMs;
            return this;
        }

        public X402SpendingEventBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public X402SpendingEventBuilder method(String method) {
            this.method = method;
            return this;
        }

        public X402SpendingEventBuilder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public X402SpendingEventBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public X402SpendingEventBuilder requestedAt(OffsetDateTime requestedAt) {
            this.requestedAt = requestedAt;
            return this;
        }

        public X402SpendingEventBuilder settledAt(OffsetDateTime settledAt) {
            this.settledAt = settledAt;
            return this;
        }

        public X402SpendingEventBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public X402SpendingEvent log() {
            return logger.log(
                buyerId, buyerName, serviceId, serviceName, serviceUrl, endpoint,
                category, network, asset, amountAtomic, txHash, paymentId,
                status, budgetId, projectId, latencyMs, errorMessage,
                method, clientIp, userAgent, requestedAt, settledAt, createdAt
            );
        }
    }
}
