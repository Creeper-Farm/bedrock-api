package com.creeperfarm.bedrockuser.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 创建权限请求参数
 */
data class PermissionCreate(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 4, max = 20, message = "Name must be between 4 and 20 characters")
    val name: String,

    @field:NotBlank(message = "Code cannot be blank")
    @field:Size(min = 8, max = 20, message = "Code must be between 8 and 20 characters")
    val code: String
)