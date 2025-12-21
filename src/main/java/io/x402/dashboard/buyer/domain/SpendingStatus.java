package io.x402.dashboard.buyer.domain;

/**
 * Status of spending events (outbound payments from buyer's perspective).
 */
public enum SpendingStatus {

    /**
     * Payment was successful and transaction confirmed on blockchain.
     */
    SUCCESS,

    /**
     * Payment is pending (initiated but not yet confirmed).
     */
    PENDING,

    /**
     * Payment failed (insufficient funds, network error, etc.).
     */
    FAILED,

    /**
     * Payment was rejected by the service or facilitator.
     */
    REJECTED,

    /**
     * Payment was refunded after successful settlement.
     */
    REFUNDED,

    /**
     * Payment required but not yet initiated (402 response received).
     */
    PAYMENT_REQUIRED
}
