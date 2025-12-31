package com.watercooler.backend.global.error.exception

import com.watercooler.backend.global.error.ErrorCode
import com.watercooler.backend.global.error.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleException(error: BusinessException): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(
            error.errorCode.code,
            null,
            LocaleContextHolder.getLocale()
        )

        return ErrorResponse.toResponseEntity(error.errorCode, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(error: Exception): ResponseEntity<ErrorResponse> {
        log.error(error.message)

        val message = messageSource.getMessage(
            ErrorCode.INTERNAL_SERVER_ERROR.code,
            null,
            LocaleContextHolder.getLocale()
        )

        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(error: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(
            ErrorCode.INVALID_INPUT_VALUE.code,
            null,
            LocaleContextHolder.getLocale()
        )

        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, message, error.bindingResult)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleException(error: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(
            ErrorCode.INVALID_INPUT_VALUE.code,
            null,
            LocaleContextHolder.getLocale()
        )

        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, message)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleException(error: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(
            ErrorCode.PAGE_NOT_FOUND.code,
            null,
            LocaleContextHolder.getLocale()
        )

        return ErrorResponse.toResponseEntity(ErrorCode.PAGE_NOT_FOUND, message)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleException(error: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(
            ErrorCode.METHOD_NOT_ALLOWED.code,
            null,
            LocaleContextHolder.getLocale()
        )

        return ErrorResponse.toResponseEntity(ErrorCode.METHOD_NOT_ALLOWED, message)
    }

}