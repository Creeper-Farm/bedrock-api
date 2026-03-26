package com.creeperfarm.bedrockcommon.model.dto

/**
 * 统一分页返回数据结构
 */
data class PageResult<T>(
    val total: Long,       // 总条数
    val list: List<T>,    // 数据列表
    val page: Int,        // 当前页码
    val size: Int         // 每页条数
) {
    companion object {
        fun <T> of(total: Long, list: List<T>, page: Int, size: Int): PageResult<T> {
            return PageResult(total, list, page, size)
        }
    }
}