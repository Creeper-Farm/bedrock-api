package com.creeperfarm.bedrockuser.model.entity

import com.creeperfarm.bedrockuser.model.enums.UserStatus
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object UserTable : LongIdTable("user") {

    // 登录用户名: 长度 50, 不可为空
    val username = varchar("username", 50).index()

    // 加密后的密码
    val password = varchar("password", 255)

    // 邮箱与手机号
    val email = varchar("email", 100).nullable().index()
    val phone = varchar("phone", 20).nullable().index()

    // 头像与简介
    val avatar = varchar("avatar", 255).default("https://default-avatar.com/user.png")
    val bio = varchar("bio", 500).nullable()

    // 账号状态
    val status = enumeration("status", UserStatus::class).default(UserStatus.NORMAL)

    // 时间
    val lastLoginTime = datetime("last_login_time").nullable()
    val createTime = datetime("create_time").defaultExpression(CurrentDateTime)
    val updateTime = datetime("update_time").defaultExpression(CurrentDateTime)

    // 软删除标志
    val deleted = bool("deleted").default(false)
}