package com.watercooler.backend.domain.auth.service

import com.watercooler.backend.domain.auth.dto.SignInRequest
import com.watercooler.backend.domain.auth.dto.SignUpRequest
import com.watercooler.backend.domain.auth.dto.TokenResponse
import com.watercooler.backend.domain.auth.entity.RefreshToken
import com.watercooler.backend.domain.auth.repository.RefreshTokenRepository
import com.watercooler.backend.domain.auth.validator.AuthValidator
import com.watercooler.backend.domain.user.mapper.UserMapper
import com.watercooler.backend.domain.user.repository.UserRepository
import com.watercooler.backend.global.config.security.JwtProperties
import com.watercooler.backend.global.config.security.JwtTokenProvider
import com.watercooler.backend.global.error.ErrorCode
import com.watercooler.backend.global.error.exception.BusinessException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userValidator: AuthValidator,

    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,

    private val passwordEncoder: PasswordEncoder,

    private val userMapper: UserMapper,

    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties
) {

    @Transactional
    fun signUp(request: SignUpRequest) {
        userValidator.validateSignUp(request.email)

        val encodedPassword = passwordEncoder.encode(request.password)!!

        val newUser = userMapper.toEntity(request, encodedPassword)

        userRepository.save(newUser)
    }

    @Transactional
    fun signIn(request: SignInRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(ErrorCode.INVALID_PASSWORD)
        }

        val accessToken = jwtTokenProvider.createAccessToken(user.email, user.role.name)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email)

        val redisToken = RefreshToken(
            email = user.email,
            token = refreshToken,
            expiration = jwtProperties.refreshTokenValidity / 1000
        )

        refreshTokenRepository.save(redisToken)

        return TokenResponse(accessToken, refreshToken)
    }

}