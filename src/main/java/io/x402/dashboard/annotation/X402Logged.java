package io.x402.dashboard.annotation;

import io.x402.dashboard.domain.AgentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for automatic x402 usage logging via AOP.
 *
 * When applied to a method, the method execution will be automatically logged
 * to the x402 dashboard with the specified parameters.
 *
 * Usage example:
 * <pre>
 * {@code
 * @X402Logged(
 *     agentId = "buyer-123",
 *     network = "eip155:84532",  // Base Sepolia (CAIP-2 format)
 *     asset = "USDC",
 *     amountAtomic = 1000000L    // 1 USDC
 * )
 * public PaymentResponse processPayment(PaymentRequest request) {
 *     // Business logic...
 *     return response;
 * }
 * }
 * </pre>
 *
 * The aspect will capture:
 * - Method name as endpoint
 * - Method execution time as latency
 * - SUCCESS status if method completes normally
 * - UNKNOWN_ERROR status if method throws an exception
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface X402Logged {

    /**
     * Tenant ID (optional). If not specified, uses default tenant ID from properties.
     */
    String tenantId() default "";

    /**
     * Agent ID (e.g., "buyer-123", "agent-456")
     */
    String agentId() default "";

    /**
     * Agent type (e.g., CLAUDE, GPT, GEMINI, CUSTOM)
     */
    AgentType agentType() default AgentType.UNKNOWN;

    /**
     * HTTP method (e.g., "POST", "GET").
     * If not specified, defaults to "METHOD_CALL".
     */
    String method() default "METHOD_CALL";

    /**
     * Endpoint or resource path.
     * If not specified, uses the method's fully qualified name.
     */
    String endpoint() default "";

    /**
     * Billing key for grouping transactions
     */
    String billingKey() default "";

    /**
     * Network identifier in CAIP-2 format.
     * Examples:
     * - "eip155:84532" (Base Sepolia)
     * - "eip155:8453" (Base Mainnet)
     * - "eip155:1" (Ethereum Mainnet)
     * - "solana:devnet" (Solana Devnet)
     */
    String network() default "";

    /**
     * Asset/token symbol (e.g., "USDC", "ETH", "SOL")
     */
    String asset() default "";

    /**
     * Amount in atomic units (e.g., 1000000 for 1 USDC with 6 decimals)
     */
    long amountAtomic() default 0L;

    /**
     * Transaction hash (optional)
     */
    String txHash() default "";
}
