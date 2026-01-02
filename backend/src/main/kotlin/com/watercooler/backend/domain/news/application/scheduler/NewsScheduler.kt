package com.watercooler.backend.domain.news.application.scheduler

import com.watercooler.backend.domain.news.application.Crawler.FmkoreaCrawler
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NewsScheduler(
    private val fmkoreaCrawler: FmkoreaCrawler
) {

    @Scheduled(fixedRate = 3600000, initialDelay = 5000)
    fun runCrawlers() {
        fmkoreaCrawler.crawl()
    }

}