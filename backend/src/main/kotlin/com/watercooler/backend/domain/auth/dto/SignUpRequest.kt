package com.watercooler.backend.domain.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpRequest(

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String

)