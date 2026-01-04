package com.watercooler.backend.domain.news.entity

import jakarta.persistence.*
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

    @Column(nullable = false, unique = true)
    val link: String,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(columnDefinition = "TEXT")
    var aiSummary: String? = null,

    val viewCount: Long = 0,
    val commentCount: Long = 0,
    val likeCount: Long = 0,

    val publishedAt: LocalDateTime = LocalDateTime.now()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

}