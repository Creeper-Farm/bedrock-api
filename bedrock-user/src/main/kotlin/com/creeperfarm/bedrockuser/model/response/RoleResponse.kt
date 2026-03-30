package com.creeperfarm.bedrockuser.model.response

import java.time.LocalDateTime

data class RoleResponse(
    val id: Long,
    val name: String,
    val code: String,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)
