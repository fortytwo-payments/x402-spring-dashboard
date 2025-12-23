package io.x402.dashboard;

import io.x402.dashboard.seller.domain.AgentType;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.logging.X402UsageLogger;
import io.x402.dashboard.seller.repository.X402UsageEventRepository;
import io.x402.dashboard.seller.service.X402UsageAggregationService;
import io.x402.dashboard.seller.service.X402UsageEventService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for generating and verifying sample data.
 * Run this test to populate the database with sample data.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class X402DemoDataTest {

    @Autowired
    private X402UsageLogger usageLogger;

    @Autowired
    private X402UsageEventService eventService;

    @Autowired
    private X402UsageAggregationService aggregationService;

    @Autowired
    private X402UsageEventRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Test X402UsageLogger basic functionality")
    void testUsageLoggerBasics() {
        // Test logSimple
        var event1 = usageLogger.logSimple("GET", "/api/test", X402UsageStatus.SUCCESS, 100L);
        assertThat(event1).isNotNull();
        assertThat(event1.getId()).isNotNull();
        assertThat(event1.getStatus()).isEqualTo(X402UsageStatus.SUCCESS);

        // Test logSuccess
        var event2 = usageLogger.logSuccess("agent-001", "POST", "/api/chat", "eip155:84532", "USDC", 1000000L, "0x123abc", 150L);
        assertThat(event2).isNotNull();
        assertThat(event2.getAgentId()).isEqualTo("agent-001");

        // Test builder
        var event3 = usageLogger.builder()
                .agentId("test-agent")
                .method("POST")
                .endpoint("/api/endpoint")
                .status(X402UsageStatus.PAYMENT_REQUIRED)
                .latencyMs(200L)
                .log();
        assertThat(event3).isNotNull();
        assertThat(event3.getStatus()).isEqualTo(X402UsageStatus.PAYMENT_REQUIRED);

        // Verify count
        assertThat(repository.count()).isEqualTo(3);
        System.out.println("Created 3 events successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test aggregation services with fresh data")
    void testAggregation() {
        // Create test data
        for (int i = 0; i < 10; i++) {
            usageLogger.logSuccess(
                    "agent-" + (i % 2), // agent-0 and agent-1
                    "POST",
                    "/api/endpoint-" + (i % 3), // 3 endpoints
                    "eip155:84532",
                    "USDC",
                    1000000L,
                    "0x" + i,
                    100L
            );
        }

        // Verify saved
        long count = repository.count();
        System.out.println("Saved " + count + " events");
        assertThat(count).isEqualTo(10);

        // Use wide time range to capture all events
        OffsetDateTime from = OffsetDateTime.now().minusYears(1);
        OffsetDateTime to = OffsetDateTime.now().plusYears(1);

        var totals = aggregationService.getOverviewTotals(null, from, to);
        System.out.println("Total count: " + totals.getTotalCount());
        System.out.println("Success count: " + totals.getSuccessCount());

        // Just verify data exists and aggregation works
        assertThat(totals.getTotalCount()).isGreaterThan(0);

        var agentStats = aggregationService.aggregateByAgent(null, from, to, X402UsageStatus.SUCCESS);
        System.out.println("Agent stats count: " + agentStats.size());

        var endpointStats = aggregationService.aggregateByEndpoint(null, from, to, X402UsageStatus.SUCCESS);
        System.out.println("Endpoint stats count: " + endpointStats.size());
    }

    @Test
    @Order(3)
    @DisplayName("Generate sample events for demo")
    void generateSampleData() {
        int count = 50;
        Random random = new Random(42);

        String[] agents = {"claude-agent-001", "gpt-agent-002", "gemini-agent-003", null};
        String[] endpoints = {"/api/v1/chat", "/api/v1/generate", "/api/v1/analyze"};
        String[] networks = {"eip155:84532", "eip155:1"};
        String[] assets = {"USDC", "ETH"};

        for (int i = 0; i < count; i++) {
            String agent = agents[random.nextInt(agents.length)];
            String endpoint = endpoints[random.nextInt(endpoints.length)];
            X402UsageStatus status = random.nextDouble() < 0.8 ? X402UsageStatus.SUCCESS : X402UsageStatus.PAYMENT_REQUIRED;

            usageLogger.builder()
                    .agentId(agent)
                    .agentType(agent != null ? AgentType.CLAUDE : null)
                    .method(random.nextBoolean() ? "POST" : "GET")
                    .endpoint(endpoint)
                    .network(status == X402UsageStatus.SUCCESS ? networks[random.nextInt(networks.length)] : null)
                    .asset(status == X402UsageStatus.SUCCESS ? assets[random.nextInt(assets.length)] : null)
                    .amountAtomic(status == X402UsageStatus.SUCCESS ? (long)(random.nextDouble() * 5_000_000) : null)
                    .status(status)
                    .latencyMs(50L + random.nextInt(200))
                    .log();
        }

        assertThat(repository.count()).isEqualTo(count);
        System.out.println("Generated " + count + " sample events");
    }

    @Test
    @Order(4)
    @DisplayName("Generate realistic demo data")
    void generateRealisticDemoData() {
        Random random = new Random();

        String[] agents = {"claude-enterprise-1", "gpt-corporate-1", "gemini-research-1"};
        String[] endpoints = {"/api/v1/chat/completions", "/api/v1/embeddings", "/api/v1/code/review"};

        int totalEvents = 0;
        for (int day = 0; day < 7; day++) {
            int eventsToday = 3 + random.nextInt(5);
            for (int i = 0; i < eventsToday; i++) {
                X402UsageStatus status = random.nextDouble() < 0.9 ? X402UsageStatus.SUCCESS : X402UsageStatus.PAYMENT_REQUIRED;

                usageLogger.builder()
                        .agentId(agents[random.nextInt(agents.length)])
                        .agentType(AgentType.CLAUDE)
                        .method("POST")
                        .endpoint(endpoints[random.nextInt(endpoints.length)])
                        .network(status == X402UsageStatus.SUCCESS ? "eip155:84532" : null)
                        .asset(status == X402UsageStatus.SUCCESS ? "USDC" : null)
                        .amountAtomic(status == X402UsageStatus.SUCCESS ? 500_000L + random.nextInt(2_000_000) : null)
                        .status(status)
                        .latencyMs(100L + random.nextInt(150))
                        .log();
                totalEvents++;
            }
        }

        System.out.println("Generated " + totalEvents + " realistic events over 7 days");
        assertThat(repository.count()).isEqualTo(totalEvents);
    }
}
