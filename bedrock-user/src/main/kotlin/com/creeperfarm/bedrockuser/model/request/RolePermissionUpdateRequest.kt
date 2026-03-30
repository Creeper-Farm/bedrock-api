package com.creeperfarm.bedrockuser.model.request

data class RolePermissionUpdateRequest(
    val permissionIds: List<Long> = emptyList()
)
