package com.watercooler.backend.domain.news.application.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import com.google.genai.types.EmbedContentConfig
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Schema
import com.google.genai.types.Type
import com.watercooler.backend.domain.news.dto.AiResponse
import com.watercooler.backend.domain.news.mapper.CommunityIssueMapper
import com.watercooler.backend.domain.news.repository.CommunityIssueRepository
import com.watercooler.backend.domain.news.repository.NewsItemRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class NewsClusteringProcessor(
    private val genAiClient: Client,
    private val newsItemRepository: NewsItemRepository,
    private val communityIssueRepository: CommunityIssueRepository,
    private val communityIssueMapper: CommunityIssueMapper,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    fun processPendingNews() {
        val pendingNews = newsItemRepository.findTop5ByIssueTitleIsNullOrderByIdAsc()

        if (pendingNews.isEmpty()) return

        for (news in pendingNews) {
            try {
                val aiResult = generateAnalysisResult(news.title, news.content)

                news.updateNewsItem(aiResult)

                val cutoff = LocalDateTime.now().minusHours(24)

                val similarIssue = communityIssueRepository.findMostSimilar(
                    news.source.name,
                    aiResult.embedding!!,
                    cutoff
                )

                if (similarIssue != null) {
                    similarIssue.updateCommunityIssue(news)

                    news.communityIssue = similarIssue
                } else {
                    val newCommunityIssue = communityIssueMapper.toEntity(news)

                    news.communityIssue = communityIssueRepository.save(newCommunityIssue)
                }

                newsItemRepository.save(news)
            } catch (exception: Exception) {
                news.issueTitle = "[ERROR] Processing Failed: ${exception.message}"

                newsItemRepository.save(news)
            }
        }
    }

    private fun generateAnalysisResult(title: String, content: String): AiResponse {
        val responseSchema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                mapOf(
                    "issueTitle" to Schema.builder().type(Type.Known.STRING).build(),
                    "summary" to Schema.builder().type(Type.Known.STRING).build(),
                    "category" to Schema.builder().type(Type.Known.STRING).build()
                )
            )
            .required(
                listOf("issueTitle", "summary", "category")
            )
            .build()

        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(responseSchema)
            .build()

        val prompt = """
            You are a strictly professional briefing assistant.
            Analyze the provided post (Title and Content) and write a **Situation Report**.
            
            # Instructions
            1. **Format**: Write in **plain text paragraphs only**. Do NOT use Markdown (no bold **, no headers #), no bullet points, and no intro/outro filler.
            2. **Language**: Write in the **SAME LANGUAGE** as the post content.
            3. **Category**: One of [POLITICS, ECONOMY, SOCIETY, LIFESTYLE, IT_SCIENCE, WORLD, ENTERTAINMENT, SPORTS, COMMUNITY].
            4. **Tone**: Use Dry, Objective, formal, and concise. (Business Report style) MUST use Plain Form. NEVER use polite forms.
            5. **Facts Only**: Strictly NO speculation, public reactions, or implications unless explicitly stated in text.
            
            # Flow of the Report
            Compose the summary in a logical flow:
            - **The Issue**: Start immediately with the core event or issue that occurred.
            - **The Details**: Explain the key facts, context, or specific numbers mentioned (e.g., reasons, figures).
            - **The Implication**: Conclude with the impact, public reaction, or main takeaway observed in the post.
            
            Output JSON only:
            {
              "issueTitle": "...",
              "summary": "...",
              "category": "IT_SCIENCE",
            }
            
            # Post Data
            - Title: $title
            - Content: $content
        """.trimIndent()

        val response = genAiClient.models.generateContent(
            "gemini-3-flash-preview",
            prompt,
            config
        )

        val rawJson = response.text()

        val aiResponse = objectMapper.readValue(rawJson, AiResponse::class.java)

        val embedConfig = EmbedContentConfig.builder()
            .taskType("CLUSTERING")
            .outputDimensionality(768)
            .build()

        val embedResponse = genAiClient.models.embedContent(
            "gemini-embedding-001",
            aiResponse.summary,
            embedConfig
        )

        aiResponse.embedding = embedResponse.embeddings().orElse(emptyList()).firstOrNull()
            ?.values()
            ?.orElse(emptyList())
            ?.let { list -> FloatArray(list.size) { list[it] } }
            ?: FloatArray(0)

        return aiResponse
    }

}