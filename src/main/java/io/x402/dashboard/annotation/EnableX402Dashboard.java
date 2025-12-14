package io.x402.dashboard.annotation;

import io.x402.dashboard.config.X402DashboardAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable x402 Dashboard for the Spring Boot application.
 *
 * Usage:
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableX402Dashboard
 * public class MyApp {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApp.class, args);
 *     }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(X402DashboardAutoConfiguration.class)
public @interface EnableX402Dashboard {

    /**
     * Dashboard root path prefix.
     * Default: "/x402-dashboard"
     */
    String path() default "/x402-dashboard";

    /**
     * Whether to use H2 in-memory database.
     * true: jdbc:h2:mem:x402-dashboard
     * false: file-based (e.g., ./x402-dashboard-db)
     */
    boolean inMemory() default true;
}
