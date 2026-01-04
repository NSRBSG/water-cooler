package com.watercooler.backend.global.config.ai

import com.google.genai.Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GenAiConfig(
    private val geminiProperties: GeminiProperties
) {

    @Bean
    fun genAiClient(): Client {
        return Client.builder()
            .apiKey(geminiProperties.apiKey)
            .build()
    }

}