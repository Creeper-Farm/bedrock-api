package com.creeperfarm.bedrockuser.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 用户注册请求参数
 */
data class UserRegister(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 6, max = 32, message = "Password must be between 6 and 32 characters")
    val password: String
)