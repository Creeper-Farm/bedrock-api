package com.creeperfarm.bedrockuser.model.dto

data class UserRoleBindRequest(
    val roleIds: List<Long> = emptyList()
)
