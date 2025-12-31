package com.watercooler.backend.global.error

import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult

data class ErrorResponse(
    val success: Boolean = false,
    val code: String,
    val message: String,
    val errors: List<FieldError> = emptyList()
) {

    data class FieldError(
        val field: String,
        val value: String
    ) {
        companion object {
            fun of(bindingResult: BindingResult): List<FieldError> {
                return bindingResult.fieldErrors.map { error ->
                    FieldError(
                        field = error.field,
                        value = (error.rejectedValue ?: "").toString()
                    )
                }
            }
        }
    }

    companion object {
        fun toResponseEntity(errorCode: ErrorCode, message: String): ResponseEntity<ErrorResponse> {
            return ResponseEntity
                .status(errorCode.status)
                .body(ErrorResponse(code = errorCode.code, message = message))
        }

        fun toResponseEntity(
            errorCode: ErrorCode,
            message: String,
            bindingResult: BindingResult
        ): ResponseEntity<ErrorResponse> {
            return ResponseEntity
                .status(errorCode.status)
                .body(ErrorResponse(code = errorCode.code, message = message, errors = FieldError.of(bindingResult)))
        }
    }

}
