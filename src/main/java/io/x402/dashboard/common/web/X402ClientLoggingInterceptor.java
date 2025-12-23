package io.x402.dashboard.common.web;

import io.x402.dashboard.seller.config.X402DashboardProperties;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.logging.X402UsageLogger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate/RestClient interceptor for automatic logging of outgoing HTTP requests.
 *
 * This interceptor captures:
 * - HTTP method and URI
 * - Request/response latency
 * - HTTP status code
 * - X-402-* headers (agentId, network, asset, amount, txHash)
 *
 * Usage:
 * <pre>
 * {@code
 * @Bean
 * public RestTemplate restTemplate(X402ClientLoggingInterceptor interceptor) {
 *     RestTemplate restTemplate = new RestTemplate();
 *     restTemplate.getInterceptors().add(interceptor);
 *     return restTemplate;
 * }
 * }
 * </pre>
 */
public class X402ClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final X402UsageLogger logger;
    private final X402DashboardProperties properties;

    public X402ClientLoggingInterceptor(X402UsageLogger logger, X402DashboardProperties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = null;
        X402UsageStatus status = X402UsageStatus.UNKNOWN_ERROR;

        try {
            // Execute the request
            response = execution.execute(request, body);

            // Map HTTP status to X402UsageStatus
            status = mapHttpStatusToUsageStatus(response.getRawStatusCode());

            return response;
        } catch (IOException e) {
            // Log error case
            status = X402UsageStatus.UNKNOWN_ERROR;
            throw e;
        } finally {
            // Calculate latency
            long latency = System.currentTimeMillis() - startTime;

            // Log the request
            logRequest(request, status, latency);
        }
    }

    private void logRequest(HttpRequest request, X402UsageStatus status, long latency) {
        String method = request.getMethod().name();
        String endpoint = request.getURI().toString();

        // Extract X-402-* headers
        String agentId = request.getHeaders().getFirst("X-402-Agent-Id");
        String network = request.getHeaders().getFirst("X-402-Network");
        String asset = request.getHeaders().getFirst("X-402-Asset");
        String amountStr = request.getHeaders().getFirst("X-402-Amount");
        String txHash = request.getHeaders().getFirst("X-402-TxHash");
        String billingKey = request.getHeaders().getFirst("X-402-Billing-Key");

        Long amountAtomic = null;
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                amountAtomic = Long.parseLong(amountStr);
            } catch (NumberFormatException e) {
                // Ignore invalid amount
            }
        }

        logger.builder()
                .tenantId(properties.getDefaultTenantId())
                .agentId(agentId)
                .method(method)
                .endpoint(endpoint)
                .billingKey(billingKey)
                .network(network)
                .asset(asset)
                .amountAtomic(amountAtomic)
                .txHash(txHash)
                .status(status)
                .latencyMs(latency)
                .log();
    }

    private X402UsageStatus mapHttpStatusToUsageStatus(int httpStatus) {
        if (httpStatus == 402) {
            return X402UsageStatus.PAYMENT_REQUIRED;
        } else if (httpStatus >= 200 && httpStatus < 300) {
            return X402UsageStatus.SUCCESS;
        } else {
            return X402UsageStatus.UNKNOWN_ERROR;
        }
    }
}
