package com.watercooler.backend.domain.auth.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
