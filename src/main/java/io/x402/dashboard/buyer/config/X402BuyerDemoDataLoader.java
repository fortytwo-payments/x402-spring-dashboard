package io.x402.dashboard.buyer.config;

import io.x402.dashboard.buyer.domain.ServiceCategory;
import io.x402.dashboard.buyer.domain.SpendingStatus;
import io.x402.dashboard.buyer.logging.X402SpendingLogger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.OffsetDateTime;
import java.util.Random;

/**
 * Demo data loader for Buyer Dashboard development and testing.
 * Activated with -Dspring.profiles.active=demo
 */
@Configuration
@Profile("demo")
public class X402BuyerDemoDataLoader {

    // Service configurations: [serviceId, serviceName, category, avgCost]
    private static final Object[][] SERVICES = {
        // AI Language Models
        {"openai-gpt4", "OpenAI GPT-4", ServiceCategory.AI_LANGUAGE_MODEL, 50000L},
        {"anthropic-claude", "Anthropic Claude", ServiceCategory.AI_LANGUAGE_MODEL, 45000L},
        {"google-gemini", "Google Gemini Pro", ServiceCategory.AI_LANGUAGE_MODEL, 40000L},

        // AI Image Generation
        {"midjourney-api", "Midjourney API", ServiceCategory.AI_IMAGE_GENERATION, 200000L},
        {"dall-e-3", "DALL-E 3", ServiceCategory.AI_IMAGE_GENERATION, 150000L},
        {"stability-ai", "Stability AI", ServiceCategory.AI_IMAGE_GENERATION, 100000L},

        // AI Voice
        {"elevenlabs-voice", "ElevenLabs Voice", ServiceCategory.AI_VOICE, 30000L},
        {"google-tts", "Google Text-to-Speech", ServiceCategory.AI_VOICE, 20000L},

        // Data APIs
        {"weather-api", "Weather API", ServiceCategory.DATA_API, 5000L},
        {"financial-data", "Financial Data API", ServiceCategory.DATA_API, 10000L},
        {"news-api", "News API", ServiceCategory.DATA_API, 8000L},

        // Storage
        {"ipfs-storage", "IPFS Storage", ServiceCategory.STORAGE, 15000L},
        {"arweave", "Arweave Permanent Storage", ServiceCategory.STORAGE, 25000L},

        // Compute
        {"aws-compute", "AWS Compute", ServiceCategory.COMPUTE, 50000L},
        {"replicate-api", "Replicate API", ServiceCategory.COMPUTE, 60000L},

        // Analytics
        {"mixpanel", "Mixpanel Analytics", ServiceCategory.ANALYTICS, 12000L},

        // Blockchain
        {"chainlink-oracle", "Chainlink Oracle", ServiceCategory.BLOCKCHAIN, 80000L},
        {"thegraph-query", "The Graph Query", ServiceCategory.BLOCKCHAIN, 20000L}
    };

    private static final String[] ENDPOINTS = {
        "/chat/completions", "/generate", "/analyze", "/query", "/process",
        "/translate", "/summarize", "/upload", "/download", "/verify"
    };

    private static final String[] NETWORKS = {
        "eip155:84532",  // Base Sepolia
        "eip155:8453",   // Base Mainnet
        "eip155:1",      // Ethereum Mainnet
        "eip155:137",    // Polygon
        "eip155:42161"   // Arbitrum
    };

    @Bean
    public CommandLineRunner loadBuyerDemoData(X402SpendingLogger logger) {
        return args -> {
            Random random = new Random(42);
            String buyerId = "demo-buyer-001";
            String buyerName = "Demo AI Agent";

            System.out.println("Loading Buyer Dashboard demo data...");

            // Generate 200 sample spending events over the last 30 days
            OffsetDateTime now = OffsetDateTime.now();
            for (int i = 0; i < 200; i++) {
                Object[] service = SERVICES[random.nextInt(SERVICES.length)];
                String serviceId = (String) service[0];
                String serviceName = (String) service[1];
                ServiceCategory category = (ServiceCategory) service[2];
                Long baseCost = (Long) service[3];

                String endpoint = ENDPOINTS[random.nextInt(ENDPOINTS.length)];
                SpendingStatus status = random.nextDouble() < 0.85 ? SpendingStatus.SUCCESS :
                        (random.nextDouble() < 0.4 ? SpendingStatus.PAYMENT_REQUIRED :
                        (random.nextDouble() < 0.5 ? SpendingStatus.FAILED : SpendingStatus.PENDING));

                String network = status == SpendingStatus.SUCCESS ? NETWORKS[random.nextInt(NETWORKS.length)] : null;
                String asset = "USDC";

                // Amount variation: ±30% of base cost
                Long amount = status == SpendingStatus.SUCCESS ?
                        (long) (baseCost * (0.7 + random.nextDouble() * 0.6)) : null;

                // Generate 64-character transaction hash (0x + 64 hex chars for 32 bytes)
                String txHash = status == SpendingStatus.SUCCESS ?
                        String.format("0x%016x%016x%016x%016x",
                            random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong()) : null;

                long latency = 50 + random.nextInt(950);

                // Distribute events over the last 30 days
                OffsetDateTime createdAt = now.minusDays(random.nextInt(30))
                        .minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60));

                // Create service URL
                String serviceUrl = "https://api." + serviceId.replace("-", "") + ".com";

                logger.builder()
                        .buyerId(buyerId)
                        .buyerName(buyerName)
                        .serviceId(serviceId)
                        .serviceName(serviceName)
                        .serviceUrl(serviceUrl)
                        .endpoint(endpoint)
                        .category(category)
                        .network(network)
                        .asset(asset)
                        .amountAtomic(amount)
                        .txHash(txHash)
                        .status(status)
                        .latencyMs(latency)
                        .createdAt(createdAt)
                        .log();
            }

            System.out.println("Buyer Dashboard demo data loaded: 200 spending events created");
            System.out.println("Services covered: " + SERVICES.length + " different services");
            System.out.println("Categories: AI, Data APIs, Storage, Compute, Analytics, Blockchain");
        };
    }
}
