package com.watercooler.backend.global.common.response

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
) {

    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(true, data)
        fun success(): ApiResponse<Unit> = ApiResponse(true)
    }

}