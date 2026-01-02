package com.watercooler.backend.domain.news.application.Crawler

import com.watercooler.backend.domain.news.entity.NewsItem
import com.watercooler.backend.domain.news.entity.Source
import com.watercooler.backend.domain.news.mapper.NewsItemMapper
import com.watercooler.backend.domain.news.repository.NewsItemRepository
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Component
class FmkoreaCrawler(
    private val newsItemRepository: NewsItemRepository,
    private val newsItemMapper: NewsItemMapper
) {

    private val BASE_URL = "https://www.fmkorea.com"
    private val TARGET_URL = "$BASE_URL/index.php?mid=best2&page="
    private val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"

    fun crawl() {
        val cookies = getCookies()
        Thread.sleep(Random.nextLong(1000, 2000))

        for (page in 1..1) {
            val links = getLinks(page, cookies)

            for (link in links) {
                if (newsItemRepository.existsByLink(link)) continue

                Thread.sleep(Random.nextLong(1000, 2000))
                val newsItem = getNewsItem(link, page, cookies)

                newsItemRepository.save(newsItem)
            }
        }
    }

    private fun getCookies(): Map<String, String> {
        try {
            val initialResponse = Jsoup.connect(BASE_URL)
                .userAgent(USER_AGENT)
                .method(Connection.Method.GET)
                .execute()

            return initialResponse.cookies()
        } catch (exception: Exception) {
            println(exception.message)
            throw Exception()
        }
    }

    private fun getLinks(page: Int, cookies: Map<String, String>): List<String> {
        try {
            val listDoc = Jsoup.connect("$TARGET_URL$page")
                .userAgent(USER_AGENT)
                .cookies(cookies)
                .get()

            return listDoc.select(".li_best2_pop0 > div > h3 > a")
                .map { element -> BASE_URL + element.attr("href") }
                .distinct()
        } catch (exception: Exception) {
            println(exception.message)
            throw Exception()
        }
    }

    private fun getNewsItem(link: String, page: Int, cookies: Map<String, String>): NewsItem {
        try {
            val doc = Jsoup.connect(link)
                .userAgent(USER_AGENT)
                .cookies(cookies)
                .referrer("$TARGET_URL$page")
                .get()


            val title = doc.selectFirst(".np_18px_span")?.text() ?: ""

            val article = doc.getElementById("bd_capture")?.selectFirst("article")

            article?.select("img")?.forEach { img ->
                val src = img.attr("src")
                img.replaceWith(TextNode("\n[IMAGE: $src]\n"))
            }

            article?.select("video")?.forEach { video ->
                val src = video.selectFirst("source")?.attr("src")
                video.replaceWith(TextNode("\n[VIDEO: $src]\n"))
            }

            val content = article?.text() ?: ""

            val viewCount = doc.selectFirst(".side.fr span:contains(조회) b")?.text()?.toLong() ?: 0
            val commentCount = doc.selectFirst(".side.fr span:contains(추천) b")?.text()?.toLong() ?: 0
            val likeCount = doc.selectFirst(".side.fr span:contains(댓글) b")?.text()?.toLong() ?: 0

            val dateText = doc.selectFirst(".date.m_no")?.text()?.trim() ?: ""
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            val publishedAt = LocalDateTime.parse(dateText, formatter)

            return newsItemMapper.toEntity(
                Source.FMKOREA,
                link,
                title,
                content,
                viewCount,
                commentCount,
                likeCount,
                publishedAt
            )
        } catch (exception: Exception) {
            println(exception.message)
            throw Exception()
        }
    }

}