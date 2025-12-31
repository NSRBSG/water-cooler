package com.watercooler.backend.global.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String
) {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002"),
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C004"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005"),

    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "U001"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002"),

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A001"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003")
}