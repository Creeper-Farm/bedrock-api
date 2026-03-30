package com.creeperfarm.bedrockcommon.model.response

/**
 * 统一分页响应体
 */
data class PageResponse<T>(
    val total: Long,
    val list: List<T>,
    val page: Int,
    val size: Int
) {
    companion object {
        fun <T> of(total: Long, list: List<T>, page: Int, size: Int): PageResponse<T> {
            return PageResponse(total, list, page, size)
        }
    }
}
