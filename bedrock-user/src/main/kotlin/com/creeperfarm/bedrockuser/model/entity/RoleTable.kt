package com.creeperfarm.bedrockuser.model.entity

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object RoleTable : LongIdTable("role") {
    val name = varchar("name", 255)
    val code = varchar("code", 255)
    val createTime = datetime("create_time").defaultExpression(CurrentDateTime)
    val updateTime = datetime("update_time").defaultExpression(CurrentDateTime)

    // 软删除标志
    val deleted = bool("deleted").default(false)
}