package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.response.UserDeviceLoginRecord
import com.creeperfarm.bedrockuser.model.entity.UserDeviceTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserDeviceRepository {

    fun findByUserIdAndDeviceId(userId: Long, deviceId: String): UserDeviceLoginRecord? {
        return activeDeviceQuery(userId, deviceId)
            .map { it.toUserDeviceLoginRecord() }
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
        UserDeviceTable.update({ activeDeviceRecordCondition(userId, deviceId) }) {
            it[UserDeviceTable.lastLoginTime] = now
            it[UserDeviceTable.loginCount] = loginCount
            it[UserDeviceTable.appVersion] = appVersion
            it[UserDeviceTable.ipAddress] = ipAddress
            it[UserDeviceTable.updateTime] = now
        }
    }

    private fun ResultRow.toUserDeviceLoginRecord() = UserDeviceLoginRecord(
        userId = this[UserDeviceTable.userId].value,
        deviceId = this[UserDeviceTable.deviceId],
        loginCount = this[UserDeviceTable.loginCount]
    )

    private fun activeDeviceQuery(userId: Long, deviceId: String): Query {
        return UserDeviceTable.selectAll()
            .where { activeDeviceRecordCondition(userId, deviceId) }
    }

    private fun activeDeviceRecordCondition(userId: Long, deviceId: String) =
        (UserDeviceTable.userId eq userId) and (UserDeviceTable.deviceId eq deviceId) and activeDeviceCondition()

    private fun activeDeviceCondition() = UserDeviceTable.deleted eq false
}
