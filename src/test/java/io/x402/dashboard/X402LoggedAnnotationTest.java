package io.x402.dashboard;

import io.x402.dashboard.seller.domain.AgentType;
import io.x402.dashboard.seller.domain.X402UsageEvent;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.repository.X402UsageEventRepository;
import io.x402.dashboard.testutil.X402TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for @X402Logged annotation.
 */
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
    "x402.dashboard.in-memory=true",
    "spring.profiles.active=test"
})
public class X402LoggedAnnotationTest {

    @Autowired
    private X402UsageEventRepository repository;

    @Autowired
    private X402TestService testService;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testSuccessfulMethodExecution() {
        // When
        String result = testService.processPayment("test-request");

        // Then
        assertThat(result).isEqualTo("Payment processed: test-request");

        List<X402UsageEvent> events = repository.findAll();
        assertThat(events).hasSize(1);

        X402UsageEvent event = events.get(0);
        assertThat(event.getAgentId()).isEqualTo("buyer-123");
        assertThat(event.getAgentType()).isEqualTo(AgentType.CUSTOM);
        assertThat(event.getMethod()).isEqualTo("POST");
        assertThat(event.getEndpoint()).isEqualTo("/api/payment");
        assertThat(event.getNetwork()).isEqualTo("eip155:84532");  // Base Sepolia
        assertThat(event.getAsset()).isEqualTo("USDC");
        assertThat(event.getAmountAtomic()).isEqualTo(1000000L);
        assertThat(event.getStatus()).isEqualTo(X402UsageStatus.SUCCESS);
        assertThat(event.getLatencyMs()).isGreaterThan(0L);
    }

    @Test
    void testMethodExecutionWithException() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            testService.processFailingPayment("error-request");
        });

        List<X402UsageEvent> events = repository.findAll();
        assertThat(events).hasSize(1);

        X402UsageEvent event = events.get(0);
        assertThat(event.getAgentId()).isEqualTo("buyer-456");
        assertThat(event.getStatus()).isEqualTo(X402UsageStatus.UNKNOWN_ERROR);
        assertThat(event.getLatencyMs()).isGreaterThan(0L);
    }

    @Test
    void testMethodWithDefaultEndpoint() {
        // When
        testService.processWithDefaultEndpoint();

        // Then
        List<X402UsageEvent> events = repository.findAll();
        assertThat(events).hasSize(1);

        X402UsageEvent event = events.get(0);
        // Should use fully qualified method name as endpoint
        assertThat(event.getEndpoint()).contains("X402TestService.processWithDefaultEndpoint");
        assertThat(event.getMethod()).isEqualTo("METHOD_CALL");  // Default method
    }
}
