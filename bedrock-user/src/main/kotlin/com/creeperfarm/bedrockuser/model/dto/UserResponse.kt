package com.creeperfarm.bedrockuser.model.dto

import com.creeperfarm.bedrockuser.model.enums.UserStatus
import java.time.LocalDateTime

/**
 * User information response DTO
 * 用于向前端返回用户信息，隐藏敏感字段（如 password）
 */
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String?,
    val avatar: String,
    val bio: String?,
    val status: UserStatus,
    val lastLoginTime: LocalDateTime?,
    val createTime: LocalDateTime
)