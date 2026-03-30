package com.creeperfarm.bedrockcommon.web

data class PageQuery(
    val page: Int,
    val size: Int,
    val offset: Long
)

fun pageQuery(page: Int, size: Int): PageQuery {
    require(page > 0) { "Page must be greater than 0" }
    require(size > 0) { "Size must be greater than 0" }
    return PageQuery(page, size, ((page - 1) * size).toLong())
}
