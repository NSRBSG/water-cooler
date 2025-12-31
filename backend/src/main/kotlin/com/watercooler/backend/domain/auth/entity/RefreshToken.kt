package com.watercooler.backend.domain.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("refreshToken")
class RefreshToken(
    @Id
    val email: String,

    val token: String,

    @TimeToLive
    val expiration: Long
)