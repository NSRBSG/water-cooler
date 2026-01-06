package com.watercooler.backend.domain.news.dto

import com.watercooler.backend.domain.news.entity.NewsCategory

data class AiResponse(
    val category: NewsCategory,
    val issueTitle: String,
    val summary: String,
    var embedding: FloatArray? = null
)
