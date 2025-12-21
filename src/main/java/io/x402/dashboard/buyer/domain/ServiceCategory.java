package io.x402.dashboard.buyer.domain;

/**
 * Categories of external services that AI agents/clients spend money on.
 * Used for cost analysis and budgeting in the Buyer Dashboard.
 */
public enum ServiceCategory {

    /**
     * AI Language Models (GPT, Claude, Gemini, etc.)
     */
    AI_LANGUAGE_MODEL,

    /**
     * AI Image Generation (DALL-E, Midjourney, Stable Diffusion, etc.)
     */
    AI_IMAGE_GENERATION,

    /**
     * AI Voice services (TTS, STT, voice cloning, etc.)
     */
    AI_VOICE,

    /**
     * AI Video services (generation, editing, etc.)
     */
    AI_VIDEO,

    /**
     * Data APIs (Weather, Stock market, News, etc.)
     */
    DATA_API,

    /**
     * Storage services (IPFS, Arweave, Filecoin, etc.)
     */
    STORAGE,

    /**
     * Compute services (Serverless functions, containers, etc.)
     */
    COMPUTE,

    /**
     * Analytics and data processing services
     */
    ANALYTICS,

    /**
     * Blockchain services (RPC nodes, indexers, etc.)
     */
    BLOCKCHAIN,

    /**
     * Other miscellaneous services
     */
    OTHER
}
