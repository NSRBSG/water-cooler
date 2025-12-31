package com.watercooler.backend.domain.user.repository

import com.watercooler.backend.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): User?

}