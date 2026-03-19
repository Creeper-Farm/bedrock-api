package com.creeperfarm.bedrockuser.model.entity

import com.creeperfarm.bedrockuser.model.enums.PermissionType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object PermissionTable : LongIdTable("permission") {
    val name = varchar("name", 255)
    val code = varchar("code", 255)
    val type = enumeration("type", PermissionType::class)
    val createTime = datetime("create_time").defaultExpression(CurrentDateTime)
    val updateTime = datetime("update_time").defaultExpression(CurrentDateTime)

    // 软删除标志
    val deleted = bool("deleted").default(false)
}