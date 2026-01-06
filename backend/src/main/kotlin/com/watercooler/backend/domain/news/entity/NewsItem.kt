package com.watercooler.backend.domain.news.entity

import com.watercooler.backend.domain.news.dto.AiResponse
import com.watercooler.backend.global.common.entity.BaseTimeEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "news_items")
class NewsItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_issue_id")
    var communityIssue: CommunityIssue? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val source: Source,

    @Enumerated(EnumType.STRING)
    var category: NewsCategory? = null,

    @Column(nullable = false, unique = true)
    val link: String,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(columnDefinition = "TEXT")
    var issueTitle: String? = null,

    @Column(columnDefinition = "TEXT")
    var summary: String? = null,

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    var embedding: FloatArray? = null,

    val viewCount: Long = 0,
    val commentCount: Long = 0,
    val likeCount: Long = 0,

    val publishedAt: LocalDateTime = LocalDateTime.now()
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateNewsItem(aiResponse: AiResponse) {
        this.category = aiResponse.category
        this.issueTitle = aiResponse.issueTitle
        this.summary = aiResponse.summary
        this.embedding = aiResponse.embedding
    }

}