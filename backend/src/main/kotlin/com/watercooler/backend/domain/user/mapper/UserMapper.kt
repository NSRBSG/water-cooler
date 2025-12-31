package com.watercooler.backend.domain.user.mapper

import com.watercooler.backend.domain.auth.dto.SignUpRequest
import com.watercooler.backend.domain.user.dto.UserResponse
import com.watercooler.backend.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun toEntity(request: SignUpRequest, encodedPassword: String): User {
        return User(
            email = request.email,
            password = encodedPassword
        )
    }

    fun toResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id ?: throw IllegalArgumentException("User Id Should not be null"),
            email = user.email
        )
    }

}