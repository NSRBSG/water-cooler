package com.watercooler.backend.domain.news.application.scheduler

import com.watercooler.backend.domain.news.application.Crawler.FmkoreaCrawler
import com.watercooler.backend.domain.news.application.processor.NewsClusteringProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NewsScheduler(
    private val fmkoreaCrawler: FmkoreaCrawler,
    private val newsClusteringProcessor: NewsClusteringProcessor
) {

    @Scheduled(fixedRate = 3600000, initialDelay = 1000)
    fun runCrawlers() {
        fmkoreaCrawler.crawl()
    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 10000)
    fun runSummary() {
        newsClusteringProcessor.processPendingNews()
    }

}