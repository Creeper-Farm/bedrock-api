package com.creeperfarm.bedrockuser.model.entity

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object UserDeviceTable : LongIdTable("user_device") {
    val userId = reference("user_id", UserTable).index()

    val deviceId = varchar("device_id", 128)
    val deviceName = varchar("device_name", 100)
    val appVersion = varchar("app_version", 32).nullable()
    val os = varchar("os", 100).nullable()
    val ipAddress = varchar("ip_address", 64).nullable()

    val userAgent = varchar("user_agent", 1024).nullable()

    val firstLoginTime = datetime("first_login_time").defaultExpression(CurrentDateTime)
    val lastLoginTime = datetime("last_login_time").defaultExpression(CurrentDateTime)

    val loginCount = integer("login_count").default(1)

    val createTime = datetime("create_time").defaultExpression(CurrentDateTime)
    val updateTime = datetime("update_time").defaultExpression(CurrentDateTime)

    // 逻辑删除标志
    val deleted = bool("deleted").default(false)

    init {
        // 核心约束：确保一个用户在同一个设备上只有一条记录
        uniqueIndex("idx_user_device_unique", userId, deviceId)
    }
}