package com.creeperfarm.bedrockcommon.model

/**
 * 统一返回值参数
 */
data class Result<out T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(data: T?): Result<T> = Result(200, "Success", data)
        fun error(code: Int, message: String): Result<Nothing> = Result(code, message, null)
    }
}