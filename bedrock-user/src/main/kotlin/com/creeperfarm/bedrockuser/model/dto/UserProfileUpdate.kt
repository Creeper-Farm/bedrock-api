package com.creeperfarm.bedrockuser.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UserProfileUpdate(
    @field:Email(message = "Email format is invalid")
    @field:Size(max = 100, message = "Email length cannot exceed 100")
    val email: String? = null,

    @field:Size(max = 20, message = "Phone length cannot exceed 20")
    val phone: String? = null,

    @field:Size(max = 255, message = "Avatar length cannot exceed 255")
    val avatar: String? = null,

    @field:Size(max = 500, message = "Bio length cannot exceed 500")
    val bio: String? = null
)
