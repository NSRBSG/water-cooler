package com.watercooler.backend.domain.news.application.scheduler

import com.watercooler.backend.domain.news.application.Crawler.FmkoreaCrawler
import com.watercooler.backend.domain.news.application.processor.AiSummaryProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NewsScheduler(
    private val fmkoreaCrawler: FmkoreaCrawler,
    private val aiSummaryProcessor: AiSummaryProcessor
) {

    @Scheduled(fixedRate = 3600000, initialDelay = 1000)
    fun runCrawlers() {
        fmkoreaCrawler.crawl()
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 10000)
    fun runSummary() {
        aiSummaryProcessor.analyzePendingNews()
    }

}