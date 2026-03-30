package com.creeperfarm.bedrockuser.model.request

data class UserRoleUpdateRequest(
    val roleIds: List<Long> = emptyList()
)
