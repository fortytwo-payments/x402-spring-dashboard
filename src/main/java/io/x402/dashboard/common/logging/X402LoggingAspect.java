package io.x402.dashboard.common.logging;

import io.x402.dashboard.common.annotation.X402Logged;
import io.x402.dashboard.seller.config.X402DashboardProperties;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.logging.X402UsageLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP Aspect for @X402Logged annotation.
 *
 * Automatically logs method executions annotated with @X402Logged to the x402 dashboard.
 * Captures method execution time, success/failure status, and metadata from the annotation.
 */
@Aspect
@Component
public class X402LoggingAspect {

    private final X402UsageLogger logger;
    private final X402DashboardProperties properties;

    public X402LoggingAspect(X402UsageLogger logger, X402DashboardProperties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    @Around("@annotation(io.x402.dashboard.common.annotation.X402Logged)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        X402UsageStatus status = X402UsageStatus.SUCCESS;
        Throwable thrownException = null;

        try {
            // Execute the method
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = X402UsageStatus.UNKNOWN_ERROR;
            thrownException = e;
            throw e;
        } finally {
            // Calculate latency
            long latency = System.currentTimeMillis() - startTime;

            // Get annotation metadata
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            X402Logged annotation = method.getAnnotation(X402Logged.class);

            if (annotation != null) {
                logEvent(annotation, method, status, latency);
            }
        }
    }

    private void logEvent(X402Logged annotation, Method method, X402UsageStatus status, long latency) {
        // Use annotation values, with defaults
        String tenantId = annotation.tenantId().isEmpty()
            ? properties.getDefaultTenantId()
            : annotation.tenantId();

        String endpoint = annotation.endpoint().isEmpty()
            ? method.getDeclaringClass().getName() + "." + method.getName()
            : annotation.endpoint();

        String agentId = annotation.agentId().isEmpty() ? null : annotation.agentId();
        String billingKey = annotation.billingKey().isEmpty() ? null : annotation.billingKey();
        String network = annotation.network().isEmpty() ? null : annotation.network();
        String asset = annotation.asset().isEmpty() ? null : annotation.asset();
        String txHash = annotation.txHash().isEmpty() ? null : annotation.txHash();
        Long amountAtomic = annotation.amountAtomic() == 0L ? null : annotation.amountAtomic();

        // Log the event
        logger.builder()
                .tenantId(tenantId)
                .agentId(agentId)
                .agentType(annotation.agentType())
                .method(annotation.method())
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
}
