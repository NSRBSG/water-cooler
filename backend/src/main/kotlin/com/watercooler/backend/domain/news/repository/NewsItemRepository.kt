package com.watercooler.backend.domain.news.repository

import com.watercooler.backend.domain.news.entity.NewsItem
import org.springframework.data.jpa.repository.JpaRepository

interface NewsItemRepository : JpaRepository<NewsItem, Long> {

    fun existsByLink(link: String): Boolean

    fun findTop5ByIssueTitleIsNullOrderByIdAsc(): List<NewsItem>

}