package com.watercooler.backend.domain.news.entity

import com.watercooler.backend.global.common.entity.BaseTimeEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

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

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    var embedding: FloatArray? = null,

    @Column(nullable = false)
    var newsItemCount: Int = 0,

    var hotScore: Double = 0.0,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "communityIssue", cascade = [CascadeType.ALL])
    val newsItems: MutableList<NewsItem> = mutableListOf()

    fun updateCommunityIssue(newsItem: NewsItem) {
        this.hotScore += calculateNewsScore(newsItem)
        this.embedding = calculateMovingAverage(this.embedding!!, this.newsItemCount, newsItem.embedding!!)

        this.newsItemCount += 1
    }

    private fun calculateNewsScore(newsItem: NewsItem): Double {
        val baseScore = 10.0

        val viewBonus = newsItem.viewCount / 1000.0
        val likeBonus = newsItem.likeCount * 0.5
        val commentBonus = newsItem.commentCount * 0.5

        return baseScore + viewBonus + likeBonus + commentBonus
    }

    private fun calculateMovingAverage(current: FloatArray, count: Int, newVec: FloatArray): FloatArray {
        val size = current.size

        if (size != newVec.size) return current

        val result = FloatArray(size)

        for (i in 0 until size) {
            result[i] = ((current[i] * count) + newVec[i]) / (count + 1)
        }

        return result
    }

}