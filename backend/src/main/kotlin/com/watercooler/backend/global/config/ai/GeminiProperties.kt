package com.watercooler.backend.global.config.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai.gemini")
data class GeminiProperties(
    val apiKey: String,
)