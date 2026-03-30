package com.creeperfarm.bedrockcommon.model.response

import java.time.LocalDateTime

/**
 * 统一接口响应体
 */
data class ApiResponse<out T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> = ApiResponse(200, "Success", data)
        fun error(code: Int, message: String): ApiResponse<Nothing> = ApiResponse(code, message, null)
    }
}
