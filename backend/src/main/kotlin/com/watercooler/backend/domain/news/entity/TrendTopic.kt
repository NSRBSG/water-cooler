package com.watercooler.backend.domain.news.entity

import com.watercooler.backend.global.common.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "trend_topics")
class TrendTopic(
    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val summary: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: NewsCategory,

    val totalScore: Double = 0.0
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "trendTopic", cascade = [CascadeType.ALL])
    val communityIssues: MutableList<CommunityIssue> = mutableListOf()

}