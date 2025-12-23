package io.x402.dashboard;

import io.x402.dashboard.seller.annotation.EnableX402Dashboard;
import io.x402.dashboard.seller.domain.X402UsageEvent;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.repository.X402UsageEventRepository;
import io.x402.dashboard.common.web.X402ClientLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for X402ClientLoggingInterceptor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "x402.dashboard.in-memory=true",
    "x402.dashboard.enable-client-auto-logging=true"
})
public class X402ClientLoggingInterceptorTest {

    @LocalServerPort
    private int port;

    @Autowired
    private X402UsageEventRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testSuccessfulRequest() {
        // Given
        String url = "http://localhost:" + port + "/test/success";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success");

        List<X402UsageEvent> events = repository.findAll();
        assertThat(events).hasSize(1);

        X402UsageEvent event = events.get(0);
        assertThat(event.getMethod()).isEqualTo("GET");
        assertThat(event.getEndpoint()).contains("/test/success");
        assertThat(event.getStatus()).isEqualTo(X402UsageStatus.SUCCESS);
        assertThat(event.getLatencyMs()).isGreaterThan(0L);
    }

    @Test
    void testPaymentRequiredRequest() {
        // Given
        String url = "http://localhost:" + port + "/test/payment-required";

        // When
        try {
            restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            // Expected to fail with 402
        }

        // Then
        List<X402UsageEvent> events = repository.findAll();
        assertThat(events).hasSize(1);

        X402UsageEvent event = events.get(0);
        assertThat(event.getMethod()).isEqualTo("GET");
        assertThat(event.getStatus()).isEqualTo(X402UsageStatus.PAYMENT_REQUIRED);
    }

    @Test
    void testRequestWithX402Headers() {
        // Given
        String url = "http://localhost:" + port + "/test/success";

        // Create RestTemplate with interceptor
        RestTemplate customRestTemplate = new RestTemplate();
        customRestTemplate.getInterceptors().add(restTemplate.getInterceptors().get(0));

        // When
        customRestTemplate.execute(url, org.springframework.http.HttpMethod.GET, request -> {
            request.getHeaders().set("X-402-Agent-Id", "buyer-123");
            request.getHeaders().set("X-402-Network", "eip155:84532");  // Base Sepolia
            request.getHeaders().set("X-402-Asset", "USDC");
            request.getHeaders().set("X-402-Amount", "1000000");
            request.getHeaders().set("X-402-TxHash", "0x123abc");
        }, response -> null);

        // Then
        List<X402UsageEvent> events = repository.findAll();
        assertThat(events).hasSize(1);

        X402UsageEvent event = events.get(0);
        assertThat(event.getAgentId()).isEqualTo("buyer-123");
        assertThat(event.getNetwork()).isEqualTo("eip155:84532");
        assertThat(event.getAsset()).isEqualTo("USDC");
        assertThat(event.getAmountAtomic()).isEqualTo(1000000L);
        assertThat(event.getTxHash()).isEqualTo("0x123abc");
        assertThat(event.getStatus()).isEqualTo(X402UsageStatus.SUCCESS);
    }

    /**
     * Test controller
     */
    @RestController
    public static class TestController {

        @GetMapping("/test/success")
        public ResponseEntity<String> success() {
            return ResponseEntity.ok("Success");
        }

        @GetMapping("/test/payment-required")
        public ResponseEntity<String> paymentRequired() {
            return ResponseEntity.status(402).body("Payment Required");
        }
    }

    @Configuration
    @EnableX402Dashboard
    public static class TestConfig {

        @Autowired
        private X402ClientLoggingInterceptor interceptor;

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(interceptor);
            return restTemplate;
        }
    }
}
