package com.watercooler.backend.domain.news.mapper

import com.watercooler.backend.domain.news.entity.CommunityIssue
import com.watercooler.backend.domain.news.entity.NewsCategory
import com.watercooler.backend.domain.news.entity.NewsItem
import org.springframework.stereotype.Component

@Component
class CommunityIssueMapper {

    fun toEntity(
        news: NewsItem
    ): CommunityIssue {
        val communityIssue = CommunityIssue(
            source = news.source,
            category = news.category ?: NewsCategory.COMMUNITY,
            title = news.issueTitle ?: "",
            summary = news.summary ?: "",
            embedding = news.embedding
        )

        communityIssue.updateCommunityIssue(news)

        return communityIssue
    }

}