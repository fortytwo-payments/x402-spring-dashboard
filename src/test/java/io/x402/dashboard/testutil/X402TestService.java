package io.x402.dashboard.testutil;

import io.x402.dashboard.annotation.X402Logged;
import io.x402.dashboard.domain.AgentType;
import org.springframework.stereotype.Service;

/**
 * Test service with @X402Logged annotated methods for testing AOP functionality.
 */
@Service
public class X402TestService {

    @X402Logged(
        agentId = "buyer-123",
        agentType = AgentType.CUSTOM,
        method = "POST",
        endpoint = "/api/payment",
        network = "eip155:84532",  // Base Sepolia (CAIP-2)
        asset = "USDC",
        amountAtomic = 1000000L
    )
    public String processPayment(String request) {
        return "Payment processed: " + request;
    }

    @X402Logged(
        agentId = "buyer-456",
        network = "eip155:1",  // Ethereum Mainnet (CAIP-2)
        asset = "ETH"
    )
    public String processFailingPayment(String request) {
        throw new RuntimeException("Payment failed");
    }

    @X402Logged(
        agentId = "buyer-789"
    )
    public void processWithDefaultEndpoint() {
        // Method with minimal annotation
    }
}
