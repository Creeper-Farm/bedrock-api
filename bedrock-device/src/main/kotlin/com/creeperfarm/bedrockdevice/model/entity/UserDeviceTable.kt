package com.creeperfarm.bedrockdevice.model.entity

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object UserDeviceTable : LongIdTable("user_device") {
    val userId = long("user_id").index()
    // deviceId 由客户端上报 deviceId 或 UA 特征计算得到，用于标识同一设备
    val deviceId = varchar("device_id", 64)
    val deviceName = varchar("device_name", 100)
    val appVersion = varchar("app_version", 32).nullable()
    val os = varchar("os", 100).nullable()
    val ipAddress = varchar("ip_address", 64).nullable()
    val userAgent = varchar("user_agent", 512).nullable()
    val firstLoginTime = datetime("first_login_time")
    val lastLoginTime = datetime("last_login_time")
    val loginCount = integer("login_count").default(1)
    val createTime = datetime("create_time").defaultExpression(CurrentDateTime)
    val updateTime = datetime("update_time").defaultExpression(CurrentDateTime)
    val deleted = bool("deleted").default(false)

    init {
        // 一对多关系下，限制同一用户的同一设备只保留一条活跃记录
        uniqueIndex(userId, deviceId)
    }
}
