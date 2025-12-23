package io.x402.dashboard.seller.domain;

/**
 * Status of x402 usage event.
 */
public enum X402UsageStatus {
    SUCCESS,              // Payment completed & resource provided
    PAYMENT_REQUIRED,     // 402 response issued
    VERIFY_FAILED,        // Payment verification failed
    SETTLE_FAILED,        // Settlement failed
    UNKNOWN_ERROR         // Other errors
}
