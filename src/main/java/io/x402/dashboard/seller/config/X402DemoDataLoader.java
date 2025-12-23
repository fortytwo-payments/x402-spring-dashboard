package io.x402.dashboard.seller.config;

import io.x402.dashboard.seller.domain.AgentType;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.logging.X402UsageLogger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.OffsetDateTime;
import java.util.Random;

/**
 * Demo data loader for development and testing.
 * Activated with -Dspring.profiles.active=demo
 */
@Configuration
@Profile("demo")
public class X402DemoDataLoader {

    @Bean
    public CommandLineRunner loadDemoData(X402UsageLogger logger) {
        return args -> {
            Random random = new Random(42);
            String[] agents = {"claude-agent-001", "gpt-agent-002", "gemini-agent-003", "custom-bot-004", null};
            String[] endpoints = {"/api/v1/chat", "/api/v1/generate", "/api/v1/analyze", "/api/v1/translate", "/api/v1/summarize"};
            String[] networks = {"eip155:84532", "eip155:1", "eip155:137"};
            String[] assets = {"USDC", "ETH", "USDT"};
            X402UsageStatus[] statuses = X402UsageStatus.values();
            AgentType[] agentTypes = AgentType.values();

            System.out.println("Loading demo data...");

            // Generate 100 sample events over the last 30 days
            OffsetDateTime now = OffsetDateTime.now();
            for (int i = 0; i < 100; i++) {
                String agent = agents[random.nextInt(agents.length)];
                String endpoint = endpoints[random.nextInt(endpoints.length)];
                X402UsageStatus status = random.nextDouble() < 0.7 ? X402UsageStatus.SUCCESS :
                        (random.nextDouble() < 0.5 ? X402UsageStatus.PAYMENT_REQUIRED : statuses[random.nextInt(statuses.length)]);
                AgentType agentType = agent != null ? agentTypes[random.nextInt(agentTypes.length)] : null;
                String network = status == X402UsageStatus.SUCCESS ? networks[random.nextInt(networks.length)] : null;
                String asset = status == X402UsageStatus.SUCCESS ? assets[random.nextInt(assets.length)] : null;
                Long amount = status == X402UsageStatus.SUCCESS ? (long) (random.nextDouble() * 10000000) : null;
                String txHash = status == X402UsageStatus.SUCCESS ? "0x" + Long.toHexString(random.nextLong()) : null;
                long latency = 50 + random.nextInt(450);

                // Distribute events over the last 30 days
                OffsetDateTime createdAt = now.minusDays(random.nextInt(30))
                        .minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60));

                logger.builder()
                        .agentId(agent)
                        .agentType(agentType)
                        .method(random.nextDouble() < 0.7 ? "POST" : "GET")
                        .endpoint(endpoint)
                        .network(network)
                        .asset(asset)
                        .amountAtomic(amount)
                        .txHash(txHash)
                        .status(status)
                        .clientIp("192.168.1." + random.nextInt(255))
                        .userAgent("Mozilla/5.0 (Demo Agent)")
                        .latencyMs(latency)
                        .createdAt(createdAt)
                        .settledAt(status == X402UsageStatus.SUCCESS ? createdAt.plusMinutes(random.nextInt(60)) : null)
                        .log();
            }

            System.out.println("Demo data loaded: 100 events created");
        };
    }
}
