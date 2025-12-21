package io.x402.dashboard.buyer;

import io.x402.dashboard.buyer.domain.ServiceCategory;
import io.x402.dashboard.buyer.domain.SpendingStatus;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import io.x402.dashboard.buyer.logging.X402SpendingLogger;
import io.x402.dashboard.buyer.repository.X402SpendingEventRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Buyer Dashboard functionality.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class X402BuyerDashboardTest {

    @Autowired
    private X402SpendingLogger spendingLogger;

    @Autowired
    private X402SpendingEventRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Test X402SpendingLogger basic functionality")
    void testSpendingLoggerBasics() {
        // Test logSuccess
        var event1 = spendingLogger.logSuccess(
            "buyer-001",
            "openai-api",
            "OpenAI API",
            "/chat/completions",
            ServiceCategory.AI_LANGUAGE_MODEL,
            "eip155:84532",  // Base Sepolia
            "USDC",
            1000000L,  // 1 USDC
            "0x123abc",
            150L
        );

        assertThat(event1).isNotNull();
        assertThat(event1.getId()).isNotNull();
        assertThat(event1.getBuyerId()).isEqualTo("buyer-001");
        assertThat(event1.getServiceId()).isEqualTo("openai-api");
        assertThat(event1.getCategory()).isEqualTo(ServiceCategory.AI_LANGUAGE_MODEL);
        assertThat(event1.getStatus()).isEqualTo(SpendingStatus.SUCCESS);
        assertThat(event1.getNetwork()).isEqualTo("eip155:84532");

        // Test logPaymentRequired
        var event2 = spendingLogger.logPaymentRequired(
            "buyer-001",
            "weather-api",
            "Weather API",
            "/forecast",
            ServiceCategory.DATA_API,
            "eip155:1",  // Ethereum Mainnet
            "USDC",
            100000L,  // 0.1 USDC
            50L
        );

        assertThat(event2).isNotNull();
        assertThat(event2.getStatus()).isEqualTo(SpendingStatus.PAYMENT_REQUIRED);

        // Test builder
        var event3 = spendingLogger.builder()
            .buyerId("buyer-002")
            .buyerName("AI Agent 2")
            .serviceId("ipfs-storage")
            .serviceName("IPFS Storage")
            .endpoint("/upload")
            .category(ServiceCategory.STORAGE)
            .network("eip155:137")  // Polygon
            .asset("USDC")
            .amountAtomic(50000L)
            .status(SpendingStatus.SUCCESS)
            .latencyMs(200L)
            .log();

        assertThat(event3).isNotNull();
        assertThat(event3.getBuyerName()).isEqualTo("AI Agent 2");
        assertThat(event3.getCategory()).isEqualTo(ServiceCategory.STORAGE);

        // Verify count
        assertThat(repository.count()).isEqualTo(3);
        System.out.println("Created 3 spending events successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test repository queries")
    void testRepositoryQueries() {
        // Create test data
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime from = now.minusDays(7);
        OffsetDateTime to = now.plusDays(1);

        for (int i = 0; i < 10; i++) {
            spendingLogger.builder()
                .buyerId("buyer-" + (i % 2))  // buyer-0 and buyer-1
                .serviceId("service-" + (i % 3))  // 3 different services
                .serviceName("Service " + (i % 3))
                .endpoint("/api/endpoint-" + i)
                .category(i < 5 ? ServiceCategory.AI_LANGUAGE_MODEL : ServiceCategory.DATA_API)
                .network("eip155:84532")
                .asset("USDC")
                .amountAtomic(1000000L * (i + 1))
                .status(SpendingStatus.SUCCESS)
                .latencyMs(100L + i * 10)
                .log();
        }

        // Test findByBuyerIdAndCreatedAtBetween
        List<X402SpendingEvent> buyer0Events = repository.findByBuyerIdAndCreatedAtBetween(
            "buyer-0", from, to
        );
        assertThat(buyer0Events).hasSize(5);
        System.out.println("Found " + buyer0Events.size() + " events for buyer-0");

        // Test sumAmountByBuyerAndTimeRange
        Long totalSpent = repository.sumAmountByBuyerAndTimeRange("buyer-0", from, to);
        assertThat(totalSpent).isGreaterThan(0);
        System.out.println("Total spent by buyer-0: " + totalSpent);

        // Test countByBuyerIdAndStatusAndCreatedAtBetween
        Long successCount = repository.countByBuyerIdAndStatusAndCreatedAtBetween(
            "buyer-0", SpendingStatus.SUCCESS, from, to
        );
        assertThat(successCount).isEqualTo(5);
        System.out.println("Success count for buyer-0: " + successCount);

        // Test findByCategoryAndCreatedAtBetween
        List<X402SpendingEvent> aiEvents = repository.findByCategoryAndCreatedAtBetween(
            ServiceCategory.AI_LANGUAGE_MODEL, from, to
        );
        assertThat(aiEvents).hasSize(5);
        System.out.println("Found " + aiEvents.size() + " AI_LANGUAGE_MODEL events");
    }

    @Test
    @Order(3)
    @DisplayName("Test different spending statuses")
    void testSpendingStatuses() {
        // SUCCESS
        spendingLogger.logSuccess(
            "buyer-001", "service-1", "Service 1", "/api/test",
            ServiceCategory.AI_LANGUAGE_MODEL,
            "eip155:84532", "USDC", 1000000L, "0xabc", 100L
        );

        // PAYMENT_REQUIRED
        spendingLogger.logPaymentRequired(
            "buyer-001", "service-2", "Service 2", "/api/test",
            ServiceCategory.DATA_API,
            "eip155:1", "USDC", 500000L, 50L
        );

        // PENDING
        spendingLogger.logPending(
            "buyer-001", "service-3", "Service 3", "/api/test",
            ServiceCategory.COMPUTE,
            "eip155:137", "USDC", 750000L, "payment-123", 75L
        );

        // FAILED
        spendingLogger.logFailed(
            "buyer-001", "service-4", "Service 4", "/api/test",
            "Insufficient funds", 120L
        );

        // REJECTED
        spendingLogger.logRejected(
            "buyer-001", "service-5", "Service 5", "/api/test",
            "Payment rejected by service", 90L
        );

        // Verify all statuses
        assertThat(repository.count()).isEqualTo(5);

        long successCount = repository.countByBuyerIdAndStatusAndCreatedAtBetween(
            "buyer-001", SpendingStatus.SUCCESS,
            OffsetDateTime.now().minusHours(1), OffsetDateTime.now().plusHours(1)
        );
        assertThat(successCount).isEqualTo(1);

        long failedCount = repository.countByBuyerIdAndStatusAndCreatedAtBetween(
            "buyer-001", SpendingStatus.FAILED,
            OffsetDateTime.now().minusHours(1), OffsetDateTime.now().plusHours(1)
        );
        assertThat(failedCount).isEqualTo(1);

        System.out.println("All spending statuses tested successfully");
    }

    @Test
    @Order(4)
    @DisplayName("Test service categories")
    void testServiceCategories() {
        ServiceCategory[] categories = {
            ServiceCategory.AI_LANGUAGE_MODEL,
            ServiceCategory.AI_IMAGE_GENERATION,
            ServiceCategory.AI_VOICE,
            ServiceCategory.DATA_API,
            ServiceCategory.STORAGE,
            ServiceCategory.COMPUTE
        };

        for (int i = 0; i < categories.length; i++) {
            spendingLogger.builder()
                .buyerId("buyer-001")
                .serviceId("service-" + i)
                .serviceName("Service " + i)
                .endpoint("/api/test")
                .category(categories[i])
                .network("eip155:84532")
                .asset("USDC")
                .amountAtomic(1000000L)
                .status(SpendingStatus.SUCCESS)
                .latencyMs(100L)
                .log();
        }

        assertThat(repository.count()).isEqualTo(categories.length);

        // Test finding by specific category
        List<X402SpendingEvent> aiLangEvents = repository.findByCategoryAndCreatedAtBetween(
            ServiceCategory.AI_LANGUAGE_MODEL,
            OffsetDateTime.now().minusHours(1),
            OffsetDateTime.now().plusHours(1)
        );
        assertThat(aiLangEvents).hasSize(1);

        List<X402SpendingEvent> storageEvents = repository.findByCategoryAndCreatedAtBetween(
            ServiceCategory.STORAGE,
            OffsetDateTime.now().minusHours(1),
            OffsetDateTime.now().plusHours(1)
        );
        assertThat(storageEvents).hasSize(1);

        System.out.println("All service categories tested successfully");
    }

    @Test
    @Order(5)
    @DisplayName("Test recent events query")
    void testRecentEvents() {
        // Create 15 events
        for (int i = 0; i < 15; i++) {
            spendingLogger.logSuccess(
                "buyer-001", "service-" + i, "Service " + i, "/api/test",
                ServiceCategory.AI_LANGUAGE_MODEL,
                "eip155:84532", "USDC", 1000000L, "0x" + i, 100L
            );
        }

        // Should return only 10 most recent
        List<X402SpendingEvent> recentEvents = repository.findTop10ByBuyerIdOrderByCreatedAtDesc("buyer-001");
        assertThat(recentEvents).hasSize(10);

        // Verify ordering (most recent first)
        for (int i = 0; i < recentEvents.size() - 1; i++) {
            assertThat(recentEvents.get(i).getCreatedAt())
                .isAfterOrEqualTo(recentEvents.get(i + 1).getCreatedAt());
        }

        System.out.println("Recent events query tested successfully");
    }
}
