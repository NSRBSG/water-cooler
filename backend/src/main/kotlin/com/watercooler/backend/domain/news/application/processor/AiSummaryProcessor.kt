package com.watercooler.backend.domain.news.application.processor

import com.google.genai.Client
import com.google.genai.types.Blob
import com.google.genai.types.Content
import com.google.genai.types.Part
import com.watercooler.backend.domain.news.entity.NewsItem
import com.watercooler.backend.domain.news.repository.NewsItemRepository
import com.watercooler.backend.global.config.ai.GeminiProperties
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Component
class AiSummaryProcessor(
    private val newsItemRepository: NewsItemRepository,
    private val genAiClient: Client,
    private val geminiProperties: GeminiProperties
) {

    @Transactional
    fun analyzePendingNews() {
        val pendingNews = newsItemRepository.findTop10ByAiSummaryIsNullOrderByIdAsc()

        if (pendingNews.isEmpty()) return

        for (item in pendingNews) {
            try {
                processItem(item)
            } catch (exception: Exception) {
                item.aiSummary = "[ERROR: ${exception.message}]"
            }
        }
    }

    private fun processItem(item: NewsItem) {
        val imageUrls = extractImageUrls(item.content)

        val parts = mutableListOf<Part>()

        val promptText = """
            You are a professional Global Trend Analyst.
            Your goal is to extract key insights and provide a high-quality summary of posts.
            
            # Context & Task
            Analyze the provided post details including title, content, and any media references.
            
            # Instructions
            1. **Language Detection**: Identify the primary language used in the 'Content' field.
            2. **Analysis**: Extract the core message, key trends, and the tone of the post.
            3. **Summary**: 
               - Write the summary in the **SAME LANGUAGE** as the detected language.
               - Focus on 'What happened', 'Why it matters', and 'Key takeaways'.
    
            # Post Information to Analyze
            - **Title**: ${item.title}
            - **Content**: ${item.content}
        """.trimIndent()

        parts.add(Part.builder().text(promptText).build())

        for (url in imageUrls) {
            try {
                val imageBytes = downloadImage(url)

                val imagePart = Part.builder()
                    .inlineData(
                        Blob.builder()
                            .data(imageBytes)
                            .mimeType("image/jpeg")
                            .build()
                    )
                    .build()

                parts.add(imagePart)
            } catch (exception: Exception) {
                println(exception.message)
            }
        }

        val content = Content.builder()
            .parts(parts)
            .build()

        val response = genAiClient.models.generateContent(
            "gemini-3-flash-preview",
            content,
            null
        )

        item.aiSummary = response.text()

        newsItemRepository.save(item)
    }

    private fun extractImageUrls(content: String): List<String> {
        val regex = Regex("\\[IMAGE: (.*?)\\]")

        return regex.findAll(content)
            .map { it.groupValues[1] }
            .toList()
    }

    private fun downloadImage(urlString: String): ByteArray {
        val connection = URI(urlString).toURL().openConnection()

        return connection.getInputStream().readBytes()
    }

}