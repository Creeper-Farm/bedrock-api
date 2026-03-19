package com.creeperfarm.bedrockuser.model.entity

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RolePermissionTable : LongIdTable("role_permission") {
    // 引用角色表 ID
    val roleId = reference("role_id", RoleTable)

    // 引用权限表 ID
    val permissionId = reference("permission_id", PermissionTable)

    // 建立联合索引
    init {
        uniqueIndex(roleId, permissionId)
    }
}