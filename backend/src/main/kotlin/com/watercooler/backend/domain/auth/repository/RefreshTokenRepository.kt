package com.watercooler.backend.domain.auth.repository

import com.watercooler.backend.domain.auth.entity.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
}