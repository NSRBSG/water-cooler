package com.watercooler.backend.domain.news.application.processor

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.Part
import com.watercooler.backend.domain.news.entity.NewsItem
import com.watercooler.backend.domain.news.repository.NewsItemRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Component
class AiSummaryProcessor(
    private val newsItemRepository: NewsItemRepository,
    private val genAiClient: Client
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
        val parts = mutableListOf<Part>()

        val promptText = """
            You are a strictly professional briefing assistant.
            Analyze the provided post (Title and Content) and write a **Situation Report**.
            
            # Instructions
            1. **Format**: Write in **plain text paragraphs only**. Do NOT use Markdown (no bold **, no headers #), no bullet points, and no intro/outro filler.
            2. **Language**: Write in the **SAME LANGUAGE** as the post content.
            3. **Tone**: Use Dry, Objective, formal, and concise. (Business Report style) MUST use Plain Form. NEVER use polite forms.
            4. **Facts Only**: Strictly NO speculation, public reactions, or implications unless explicitly stated in text.
            
            # Flow of the Report
            Compose the summary in a logical flow:
            - **The Issue**: Start immediately with the core event or issue that occurred.
            - **The Details**: Explain the key facts, context, or specific numbers mentioned (e.g., reasons, figures).
            - **The Implication**: Conclude with the impact, public reaction, or main takeaway observed in the post.
            
            # Post Data
            - Title: ${item.title}
            - Content: ${item.content}
        """.trimIndent()

        parts.add(Part.builder().text(promptText).build())

//        val imageUrls = extractImageUrls(item.content)
//
//        for (url in imageUrls) {
//            try {
//                val imageBytes = downloadImage(url)
//
//                val imagePart = Part.builder()
//                    .inlineData(
//                        Blob.builder()
//                            .data(imageBytes)
//                            .mimeType("image/jpeg")
//                            .build()
//                    )
//                    .build()
//
//                parts.add(imagePart)
//            } catch (exception: Exception) {
//                println(exception.message)
//            }
//        }

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
        val effectiveUrl = if (urlString.startsWith("//")) "https:$urlString" else urlString

        val connection = URI(effectiveUrl).toURL().openConnection()

        return connection.getInputStream().readBytes()
    }

}