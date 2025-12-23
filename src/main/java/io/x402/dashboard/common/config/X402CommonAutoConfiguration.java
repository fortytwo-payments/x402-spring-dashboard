package io.x402.dashboard.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Common auto-configuration for x402 Dashboard.
 *
 * This configuration provides shared components used by both Seller and Buyer dashboards:
 * - X402Logged annotation
 * - X402LoggingAspect (AOP for @X402Logged)
 * - X402ClientLoggingInterceptor (RestTemplate/RestClient interceptor)
 */
@Configuration
@ComponentScan(basePackages = "io.x402.dashboard.common")
@EnableAspectJAutoProxy
public class X402CommonAutoConfiguration {
    // Common beans are auto-scanned from io.x402.dashboard.common package
}
