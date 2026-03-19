package com.creeperfarm.bedrockuser.model.entity

import com.creeperfarm.bedrockuser.model.entity.UserTable
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object UserRoleTable : LongIdTable("user_role") {
    val userId = reference("user_id", UserTable)
    val roleId = reference("role_id", RoleTable)

    init {
        uniqueIndex(userId, roleId)
    }
}