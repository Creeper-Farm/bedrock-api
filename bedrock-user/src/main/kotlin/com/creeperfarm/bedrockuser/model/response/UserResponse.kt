package com.creeperfarm.bedrockuser.model.response

import com.creeperfarm.bedrockuser.model.enums.UserStatus
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String?,
    val avatar: String?,
    val bio: String?,
    val status: UserStatus,
    val lastLoginTime: LocalDateTime?,
    val createTime: LocalDateTime
)
