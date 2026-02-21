package com.creeperfarm.bedrockdevice.repository

import com.creeperfarm.bedrockdevice.model.dto.DeviceLoginRecord
import com.creeperfarm.bedrockdevice.model.entity.UserDeviceTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserDeviceRepository {

    // 判断该用户-设备是否已有登录记录
    fun findByUserIdAndDeviceId(userId: Long, deviceId: String): DeviceLoginRecord? {
        return UserDeviceTable.selectAll()
            .where { activeDeviceCondition(userId, deviceId) }
            .map { it.toDeviceLoginRecord() }
            .singleOrNull()
    }

    fun createLoginRecord(
        userId: Long,
        deviceId: String,
        deviceName: String,
        appVersion: String?,
        os: String?,
        ipAddress: String?,
        userAgent: String?,
        now: LocalDateTime
    ) {
        // 首次登录该设备：写入 first/last 登录时间并初始化计数
        UserDeviceTable.insertAndGetId {
            it[UserDeviceTable.userId] = userId
            it[UserDeviceTable.deviceId] = deviceId
            it[UserDeviceTable.deviceName] = deviceName
            it[UserDeviceTable.appVersion] = appVersion
            it[UserDeviceTable.os] = os
            it[UserDeviceTable.ipAddress] = ipAddress
            it[UserDeviceTable.userAgent] = userAgent
            it[UserDeviceTable.firstLoginTime] = now
            it[UserDeviceTable.lastLoginTime] = now
            it[UserDeviceTable.loginCount] = 1
            it[UserDeviceTable.updateTime] = now
        }
    }

    fun updateLoginRecord(
        userId: Long,
        deviceId: String,
        loginCount: Int,
        appVersion: String?,
        ipAddress: String?,
        now: LocalDateTime
    ) {
        // 非首次登录该设备：按约定仅更新计数、登录时间、版本和 IP
        UserDeviceTable.update({ activeDeviceCondition(userId, deviceId) }) {
            it[UserDeviceTable.lastLoginTime] = now
            it[UserDeviceTable.loginCount] = loginCount
            it[UserDeviceTable.appVersion] = appVersion
            it[UserDeviceTable.ipAddress] = ipAddress
            it[UserDeviceTable.updateTime] = now
        }
    }

    private fun ResultRow.toDeviceLoginRecord() = DeviceLoginRecord(
        userId = this[UserDeviceTable.userId],
        deviceId = this[UserDeviceTable.deviceId],
        loginCount = this[UserDeviceTable.loginCount]
    )

    private fun activeDeviceCondition(userId: Long, deviceId: String) =
        (UserDeviceTable.userId eq userId) and
            (UserDeviceTable.deviceId eq deviceId) and
            (UserDeviceTable.deleted eq false)
}
