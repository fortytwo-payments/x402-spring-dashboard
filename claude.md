# Claude Code Working Guide

> ⚠️ **IMPORTANT**: Please read this file first before starting work on this project.

## Project Overview

**X402 Dashboard for Spring Boot** is a Spring Boot Starter library for monitoring and analyzing HTTP 402 Payment Required transactions.

- **Project Name**: x402-spring-dashboard
- **Group ID**: io.github.fortytwo-payments
- **Version**: 0.0.1-SNAPSHOT
- **x402 Protocol Version**: **v2** (Using CAIP-2 network format)
- **Java Version**: 17+
- **Spring Boot Version**: 3.4+

## 🔴 Pre-Work Checklist

### 1. x402 Protocol Version
- This project follows **x402 v2** specification
- Never use v1 format network identifiers

### 2. Network Identifier Format (CAIP-2)
**You MUST use CAIP-2 format:**

```java
// ✅ Correct Format (v2 - CAIP-2)
"eip155:84532"      // Base Sepolia
"eip155:8453"       // Base Mainnet
"eip155:1"          // Ethereum Mainnet
"eip155:11155111"   // Ethereum Sepolia
"eip155:137"        // Polygon Mainnet
"eip155:42161"      // Arbitrum One
"solana:devnet"     // Solana Devnet
"solana:mainnet"    // Solana Mainnet

// ❌ Wrong Format (v1 - NEVER USE)
"base-sepolia"
"ethereum-sepolia"
"polygon"
"arbitrum"
```

**Format Rules:**
- EVM chains: `eip155:<chainId>`
- Solana: `solana:<network>`

### 3. Coding Guidelines
- Always use CAIP-2 format when hardcoding network identifiers
- Apply CAIP-2 format to example code, test code, and documentation
- Check CAIP-2 standard when adding new networks: https://github.com/ChainAgnostic/CAIPs/blob/main/CAIPs/caip-2.md

## Project Structure

```
x402-spring-dashboard/
├── src/main/java/io/x402/dashboard/
│   ├── annotation/
│   │   └── EnableX402Dashboard.java          # Activation annotation
│   ├── config/
│   │   ├── X402DashboardAutoConfiguration.java
│   │   ├── X402DashboardProperties.java
│   │   └── X402DemoDataLoader.java           # Demo data loader
│   ├── domain/
│   │   ├── X402UsageEvent.java               # Main entity
│   │   ├── X402UsageStatus.java              # Status enum
│   │   └── AgentType.java                    # Agent type enum
│   ├── repository/
│   │   └── X402UsageEventRepository.java
│   ├── service/
│   │   ├── X402UsageEventService.java
│   │   ├── X402UsageAggregationService.java  # Analytics service
│   │   └── dto/                              # DTO classes
│   ├── logging/
│   │   └── X402UsageLogger.java              # Public API (user-facing)
│   └── web/
│       ├── X402DashboardController.java      # Thymeleaf views
│       ├── X402DashboardRestController.java  # REST API
│       ├── X402DemoDataController.java       # Demo data generation
│       └── X402UsageLoggingInterceptor.java  # Auto-logging
├── src/main/resources/
│   └── templates/x402-dashboard/             # Thymeleaf templates
└── src/test/java/io/x402/dashboard/
    ├── ApplicationTests.java
    └── X402DemoDataTest.java
```

## Core Components

### 1. X402UsageEvent (Domain Entity)
**Location**: `src/main/java/io/x402/dashboard/domain/X402UsageEvent.java`

**Important Fields:**
- `network` (String): **Stored in CAIP-2 format** (e.g., "eip155:84532")
- `asset` (String): Token symbol (e.g., "USDC", "ETH")
- `amountAtomic` (Long): Amount in atomic units
- `status` (X402UsageStatus): SUCCESS, PAYMENT_REQUIRED, VERIFY_FAILED, SETTLE_FAILED, UNKNOWN_ERROR
- `agentType` (AgentType): CLAUDE, GPT, GEMINI, CUSTOM, UNKNOWN

### 2. X402UsageLogger (Public API)
**Location**: `src/main/java/io/x402/dashboard/logging/X402UsageLogger.java`

User-facing logging API.

**Usage Example:**
```java
@Autowired
private X402UsageLogger usageLogger;

// Simple logging
usageLogger.logSuccess(
    "agent-123",           // agentId
    "POST",                // method
    "/api/chat",           // endpoint
    "eip155:84532",        // network (CAIP-2 required!)
    "USDC",                // asset
    1000000L,              // amountAtomic
    "0x123...",            // txHash
    150L                   // latencyMs
);

// Builder pattern
usageLogger.builder()
    .agentId("agent-123")
    .method("POST")
    .endpoint("/api/chat")
    .network("eip155:84532")  // CAIP-2 format!
    .asset("USDC")
    .amountAtomic(1000000L)
    .status(X402UsageStatus.SUCCESS)
    .latencyMs(150L)
    .log();
```

### 3. Demo Data Generation
**Files:**
- `X402DemoDataLoader.java` - Auto-generates when Spring Profile "demo" is active
- `X402DemoDataController.java` - Dynamic generation via REST API
- `X402DemoDataTest.java` - Test data generation

**Note**: All network identifiers in demo data must use CAIP-2 format!

## Development Workflow

### When Adding New Features
1. **Read this file (claude.md) first**
2. Verify CAIP-2 format for network-related code
3. Implement feature
4. Write tests (verify CAIP-2 format for network identifiers)
5. Run `./gradlew test`
6. Update README.md (if needed)

### When Modifying Code
1. Check network identifier format in existing code
2. If v1 format is found, immediately change to CAIP-2
3. Update related test code as well
4. Update documentation (README.md, claude-readme.md) as well

### Running Tests
```bash
# All tests
./gradlew test

# Specific test
./gradlew test --tests X402DemoDataTest

# Run with demo mode
./gradlew bootRun --args='--spring.profiles.active=demo'
```

## Configuration

### application.properties
```properties
# Dashboard paths
x402.dashboard.path=/x402-dashboard
x402.dashboard.api-path=/x402-dashboard/api

# H2 Database
x402.dashboard.in-memory=true
x402.dashboard.file-path=./x402-dashboard-db

# Auto-logging (default: false)
x402.dashboard.enable-auto-logging=false

# Multi-tenancy
x402.dashboard.default-tenant-id=
```

## Forbidden Patterns

### ❌ Things You Should NEVER Do

1. **Using v1 network format**
```java
// ❌ Wrong
.network("base-sepolia")
.network("ethereum")

// ✅ Correct
.network("eip155:84532")
.network("eip155:1")
```

2. **Hardcoding network identifiers without comments**
```java
// ❌ Wrong
String[] networks = {"eip155:84532", "eip155:1"};

// ✅ Correct
String[] networks = {
    "eip155:84532",  // Base Sepolia
    "eip155:1"       // Ethereum Mainnet
};
```

3. **Leaving v1 examples in documentation**
- Both README.md and claude-readme.md must use v2 format only

## Network Chain ID Reference

Commonly used networks in CAIP-2 format:

| Network | CAIP-2 Format | Chain ID | Note |
|---------|---------------|----------|------|
| Base Sepolia | `eip155:84532` | 84532 | Testnet |
| Base Mainnet | `eip155:8453` | 8453 | Mainnet |
| Ethereum Mainnet | `eip155:1` | 1 | Mainnet |
| Ethereum Sepolia | `eip155:11155111` | 11155111 | Testnet |
| Polygon Mainnet | `eip155:137` | 137 | Mainnet |
| Arbitrum One | `eip155:42161` | 42161 | Mainnet |
| Solana Devnet | `solana:devnet` | - | Testnet |
| Solana Mainnet | `solana:mainnet` | - | Mainnet |

**When adding new networks**: Check Chain ID at https://chainlist.org/

## Migration History

### 2024-12-21: v1 → v2 Migration
- Changed all network identifiers to CAIP-2 format
- Updated documentation (README.md, claude-readme.md)
- Updated test code
- Updated build.gradle description ("X402 v2 Dashboard...")

**Modified Files:**
- X402DemoDataLoader.java
- X402DemoDataController.java
- X402UsageLogger.java (documentation comments)
- X402UsageEvent.java (field comments)
- X402DemoDataTest.java
- README.md
- claude-readme.md
- build.gradle

## Related Documentation

- **README.md**: Official user documentation
- **claude-readme.md**: Detailed design document (Korean)
- **x402 v1 Spec**: https://github.com/coinbase/x402/blob/main/specs/x402-specification-v1.md
- **x402 v2 Spec**: https://github.com/coinbase/x402/blob/main/specs/x402-specification-v2.md
- **CAIP-2 Spec**: https://github.com/ChainAgnostic/CAIPs/blob/main/CAIPs/caip-2.md

## Common Mistakes Checklist

Before completing your work, verify:

- [ ] If you added/modified network identifiers, are they in CAIP-2 format?
- [ ] Did you add comments to network identifiers in example code?
- [ ] Does test code use CAIP-2 format?
- [ ] If you modified documentation, is there no v1 format remaining?
- [ ] Did you run `./gradlew test`?
- [ ] If you added new features, did you update README.md?

## Critical Notes

### x402 Protocol Overall Architecture
This project serves only as a **dashboard (monitoring)** component.

**Related Projects:**
- `x402-facilitator` - Payment verification and settlement service (/verify, /settle)
- `x402-resource-server` - Resource server (402 Payment Required response)
- `x402-client` - Client (payment flow implementation)

**Current Status:**
- ✅ x402-spring-dashboard: v2 complete
- ⚠️ x402-facilitator: v1 (migration needed)
- ⚠️ x402-resource-server: v1 (migration needed)
- ⚠️ x402-client: v1 (migration needed)

### Database Schema
**H2 In-Memory Database** used (development/testing)

**Main Table:** `x402_usage_event`
- `network` column: VARCHAR, stores CAIP-2 format
- Auto-generated via JPA (ddl-auto=update)

### Access Paths
- Dashboard UI: `http://localhost:8080/x402-dashboard`
- REST API: `http://localhost:8080/x402-dashboard/api/*`
- H2 Console: `http://localhost:8080/h2-console`

---

## Final Check

Before starting work:
1. ✅ Have you read this file completely?
2. ✅ Do you understand CAIP-2 network format?
3. ✅ Do you know that using v1 format is forbidden?

**Happy Coding! 🚀**

---

*Last Updated: 2024-12-21*
*x402 Protocol Version: v2*
*Claude Code Guide Version: 1.0*
