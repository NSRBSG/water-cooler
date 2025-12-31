package com.watercooler.backend.global.config.security

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {

    private val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(email: String, role: String): String {
        return buildToken(email, jwtProperties.accessTokenValidity)
            .claim("role", role)
            .compact()
    }

    fun createRefreshToken(email: String): String {
        return buildToken(email, jwtProperties.refreshTokenValidity)
            .compact()
    }

    private fun buildToken(email: String, validity: Long): JwtBuilder {
        val now = Date()
        val expiry = Date(now.time + validity)

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
    }

    fun getEmail(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun getRole(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload["role"]
            .toString()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (exception: Exception) {
            return false
        }
    }

}