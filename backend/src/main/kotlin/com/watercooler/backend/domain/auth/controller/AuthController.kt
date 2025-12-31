package com.watercooler.backend.domain.auth.controller

import com.watercooler.backend.domain.auth.dto.SignInRequest
import com.watercooler.backend.domain.auth.dto.SignUpRequest
import com.watercooler.backend.domain.auth.dto.TokenResponse
import com.watercooler.backend.domain.auth.service.AuthService
import com.watercooler.backend.global.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ApiResponse<Unit> {

        authService.signUp(request)

        return ApiResponse.success()
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody @Valid request: SignInRequest): ApiResponse<TokenResponse> {

        val tokens = authService.signIn(request)

        return ApiResponse.success(tokens)
    }

}