package io.x402.dashboard.web;

import io.x402.dashboard.config.X402DashboardProperties;
import io.x402.dashboard.domain.X402UsageStatus;
import io.x402.dashboard.logging.X402UsageLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for automatic request logging.
 * Logs all requests with their status codes and latency.
 */
@Component
public class X402UsageLoggingInterceptor implements HandlerInterceptor {

    private final X402UsageLogger logger;
    private final X402DashboardProperties properties;

    private static final String START_TIME_ATTR = "X402_START_TIME";

    public X402UsageLoggingInterceptor(X402UsageLogger logger, X402DashboardProperties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        // Skip logging for dashboard endpoints
        String uri = request.getRequestURI();
        if (uri.startsWith(properties.getPath()) || uri.startsWith(properties.getApiPath())) {
            return;
        }

        int status = response.getStatus();
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long latency = startTime != null ? System.currentTimeMillis() - startTime : 0;

        X402UsageStatus usageStatus = mapHttpStatusToUsageStatus(status);

        String method = request.getMethod();
        String endpoint = uri;
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        // Extract x402-specific headers if present
        String agentId = request.getHeader("X-402-Agent-Id");
        String network = request.getHeader("X-402-Network");
        String asset = request.getHeader("X-402-Asset");

        logger.builder()
                .tenantId(properties.getDefaultTenantId())
                .agentId(agentId)
                .method(method)
                .endpoint(endpoint)
                .network(network)
                .asset(asset)
                .status(usageStatus)
                .clientIp(clientIp)
                .userAgent(userAgent != null && userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent)
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

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs (take the first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
