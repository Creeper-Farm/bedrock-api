package com.creeperfarm.bedrockuser.model.entity

import com.creeperfarm.bedrockuser.model.enums.UserStatus
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object UserTable : LongIdTable("user") {
    val username = varchar("username", 50).index()
    val password = varchar("password", 255)
    val email = varchar("email", 100).nullable().index()
    val phone = varchar("phone", 20).nullable().index()
    val avatar = varchar("avatar", 255).default("https://default-avatar.com/user.png")
    val bio = varchar("bio", 500).nullable()
    val status = enumeration("status", UserStatus::class).default(UserStatus.NORMAL)
    val lastLoginTime = datetime("last_login_time").nullable()
    val createTime = datetime("create_time").defaultExpression(CurrentDateTime)
    val updateTime = datetime("update_time").defaultExpression(CurrentDateTime)
    val deleted = bool("deleted").default(false)
}
