package com.watercooler.backend.domain.news.repository

import com.watercooler.backend.domain.news.entity.CommunityIssue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface CommunityIssueRepository : JpaRepository<CommunityIssue, Long> {

    @Query(
        """
        SELECT * FROM community_issues
        WHERE source = :source
        AND created_at > :cutoff
        AND (embedding <=> CAST(:vector AS vector)) < 0.03
        ORDER BY embedding <=> CAST(:vector AS vector) ASC
        LIMIT 1
    """, nativeQuery = true
    )
    fun findMostSimilar(
        @Param("source") source: String,
        @Param("vector") vector: FloatArray,
        @Param("cutoff") cutoff: LocalDateTime
    ): CommunityIssue?

}