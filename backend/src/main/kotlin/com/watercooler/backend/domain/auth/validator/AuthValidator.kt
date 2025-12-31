package com.watercooler.backend.domain.auth.validator

import com.watercooler.backend.domain.user.repository.UserRepository
import com.watercooler.backend.global.error.ErrorCode
import com.watercooler.backend.global.error.exception.BusinessException
import org.springframework.stereotype.Component

@Component
class AuthValidator(
    private val userRepository: UserRepository
) {

    fun validateSignUp(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw BusinessException(ErrorCode.EMAIL_DUPLICATION)
        }
    }

}