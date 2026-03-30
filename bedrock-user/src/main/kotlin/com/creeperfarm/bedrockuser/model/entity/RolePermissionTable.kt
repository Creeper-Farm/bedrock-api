package com.creeperfarm.bedrockuser.model.entity

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RolePermissionTable : LongIdTable("role_permission") {
    val roleId = reference("role_id", RoleTable)
    val permissionId = reference("permission_id", PermissionTable)

    init {
        uniqueIndex(roleId, permissionId)
    }
}
