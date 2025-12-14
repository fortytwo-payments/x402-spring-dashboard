package io.x402.dashboard.web;

import io.x402.dashboard.domain.AgentType;
import io.x402.dashboard.domain.X402UsageStatus;
import io.x402.dashboard.logging.X402UsageLogger;
import io.x402.dashboard.service.X402UsageEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Random;

/**
 * Controller for generating demo/sample data.
 * Use this for testing and demonstration purposes.
 */
@RestController
@RequestMapping("${x402.dashboard.api-path:/x402-dashboard/api}/demo")
public class X402DemoDataController {

    private final X402UsageLogger usageLogger;
    private final X402UsageEventService eventService;

    public X402DemoDataController(X402UsageLogger usageLogger, X402UsageEventService eventService) {
        this.usageLogger = usageLogger;
        this.eventService = eventService;
    }

    /**
     * Generate sample data.
     *
     * @param count Number of events to generate (default: 100)
     * @param days Number of days to spread events over (default: 30)
     * @return Summary of generated data
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateSampleData(
            @RequestParam(defaultValue = "100") int count,
            @RequestParam(defaultValue = "30") int days) {

        Random random = new Random();

        String[] agents = {
            "claude-agent-001", "claude-agent-002",
            "gpt-agent-001", "gpt-agent-002",
            "gemini-agent-001",
            "custom-bot-001", "custom-bot-002",
            null // Some requests without agent ID
        };

        String[] endpoints = {
            "/api/v1/chat/completions",
            "/api/v1/embeddings",
            "/api/v1/images/generate",
            "/api/v1/audio/transcribe",
            "/api/v1/code/analyze",
            "/api/v1/translate",
            "/api/v1/summarize",
            "/api/v1/search"
        };

        String[] networks = {"base-sepolia", "base-mainnet", "ethereum", "polygon", "arbitrum"};
        String[] assets = {"USDC", "USDT", "ETH", "WETH"};
        String[] methods = {"POST", "GET", "PUT"};
        AgentType[] agentTypes = AgentType.values();

        int successCount = 0;
        int paymentRequiredCount = 0;
        int errorCount = 0;
        long totalAmount = 0;

        for (int i = 0; i < count; i++) {
            // Determine status with realistic distribution (70% success, 20% payment required, 10% error)
            double statusRoll = random.nextDouble();
            X402UsageStatus status;
            if (statusRoll < 0.70) {
                status = X402UsageStatus.SUCCESS;
                successCount++;
            } else if (statusRoll < 0.90) {
                status = X402UsageStatus.PAYMENT_REQUIRED;
                paymentRequiredCount++;
            } else {
                status = random.nextBoolean() ? X402UsageStatus.VERIFY_FAILED : X402UsageStatus.SETTLE_FAILED;
                errorCount++;
            }

            String agent = agents[random.nextInt(agents.length)];
            String endpoint = endpoints[random.nextInt(endpoints.length)];
            String method = methods[random.nextInt(methods.length)];
            AgentType agentType = agent != null ? agentTypes[random.nextInt(agentTypes.length)] : null;

            // Only successful transactions have network, asset, amount, txHash
            String network = status == X402UsageStatus.SUCCESS ? networks[random.nextInt(networks.length)] : null;
            String asset = status == X402UsageStatus.SUCCESS ? assets[random.nextInt(assets.length)] : null;
            Long amount = status == X402UsageStatus.SUCCESS ? (long) (random.nextDouble() * 10_000_000) + 100_000 : null; // 0.1 to 10 USDC
            String txHash = status == X402UsageStatus.SUCCESS ? "0x" + String.format("%016x", random.nextLong()) + String.format("%016x", random.nextLong()) : null;

            if (amount != null) {
                totalAmount += amount;
            }

            // Random time within the specified days
            int daysAgo = random.nextInt(days);
            int hoursAgo = random.nextInt(24);
            int minutesAgo = random.nextInt(60);
            OffsetDateTime createdAt = OffsetDateTime.now()
                    .minusDays(daysAgo)
                    .minusHours(hoursAgo)
                    .minusMinutes(minutesAgo);

            long latency = 50 + random.nextInt(450); // 50-500ms

            usageLogger.builder()
                    .agentId(agent)
                    .agentType(agentType)
                    .method(method)
                    .endpoint(endpoint)
                    .network(network)
                    .asset(asset)
                    .amountAtomic(amount)
                    .txHash(txHash)
                    .status(status)
                    .clientIp("192.168.1." + random.nextInt(255))
                    .userAgent("DemoAgent/1.0 (" + (agent != null ? agent : "unknown") + ")")
                    .latencyMs(latency)
                    .settledAt(status == X402UsageStatus.SUCCESS ? createdAt.plusSeconds(random.nextInt(10)) : null)
                    .log();
        }

        return ResponseEntity.ok(Map.of(
                "message", "Sample data generated successfully",
                "totalEvents", count,
                "successCount", successCount,
                "paymentRequiredCount", paymentRequiredCount,
                "errorCount", errorCount,
                "totalAmountAtomic", totalAmount,
                "daysSpread", days
        ));
    }

    /**
     * Clear all demo data.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAllData() {
        eventService.deleteAll();
        return ResponseEntity.ok(Map.of("message", "All data cleared successfully"));
    }

    /**
     * Generate a single test event with specific parameters.
     */
    @PostMapping("/event")
    public ResponseEntity<Map<String, Object>> createSingleEvent(
            @RequestParam(required = false) String agentId,
            @RequestParam(required = false, defaultValue = "POST") String method,
            @RequestParam(required = false, defaultValue = "/api/v1/test") String endpoint,
            @RequestParam(required = false, defaultValue = "SUCCESS") String status,
            @RequestParam(required = false, defaultValue = "base-sepolia") String network,
            @RequestParam(required = false, defaultValue = "USDC") String asset,
            @RequestParam(required = false, defaultValue = "1000000") Long amount) {

        X402UsageStatus usageStatus = X402UsageStatus.valueOf(status);

        var event = usageLogger.builder()
                .agentId(agentId != null ? agentId : "test-agent-" + System.currentTimeMillis())
                .method(method)
                .endpoint(endpoint)
                .network(usageStatus == X402UsageStatus.SUCCESS ? network : null)
                .asset(usageStatus == X402UsageStatus.SUCCESS ? asset : null)
                .amountAtomic(usageStatus == X402UsageStatus.SUCCESS ? amount : null)
                .txHash(usageStatus == X402UsageStatus.SUCCESS ? "0x" + Long.toHexString(System.currentTimeMillis()) : null)
                .status(usageStatus)
                .clientIp("127.0.0.1")
                .userAgent("TestClient/1.0")
                .latencyMs(100L)
                .settledAt(usageStatus == X402UsageStatus.SUCCESS ? OffsetDateTime.now() : null)
                .log();

        return ResponseEntity.ok(Map.of(
                "message", "Event created successfully",
                "eventId", event.getId(),
                "status", event.getStatus().name()
        ));
    }
}
