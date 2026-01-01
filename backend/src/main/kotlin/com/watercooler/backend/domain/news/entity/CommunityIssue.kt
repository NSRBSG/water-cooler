package com.watercooler.backend.domain.news.entity

import jakarta.persistence.*

@Entity
@Table(name = "community_issues")
class CommunityIssue(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_topic_id")
    var trendTopic: TrendTopic? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val source: Source,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: NewsCategory,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val summary: String,

    val hotScore: Double = 0.0,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "communityIssue", cascade = [CascadeType.ALL])
    val newsItems: MutableList<NewsItem> = mutableListOf()

}