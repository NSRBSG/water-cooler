package com.watercooler.backend.domain.news.mapper

import com.watercooler.backend.domain.news.entity.NewsItem
import com.watercooler.backend.domain.news.entity.Source
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NewsItemMapper {

    fun toEntity(
        source: Source,
        link: String,
        title: String,
        content: String,
        viewCount: Long,
        commentCount: Long,
        likeCount: Long,
        publishedAt: LocalDateTime
    ): NewsItem {
        return NewsItem(
            source = source,
            link = link,
            title = title,
            content = content,
            viewCount = viewCount,
            commentCount = commentCount,
            likeCount = likeCount,
            publishedAt = publishedAt,
        )
    }

}