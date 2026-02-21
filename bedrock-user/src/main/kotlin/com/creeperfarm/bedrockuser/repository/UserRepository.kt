package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.dto.UserRegister
import com.creeperfarm.bedrockuser.model.dto.UserProfileUpdate
import com.creeperfarm.bedrockuser.model.dto.UserResponse
import com.creeperfarm.bedrockuser.model.entity.UserTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserRepository {

    /**
     * 查询用户 (DSL)
     */
    fun findByUserId(userId: Long): UserResponse? {
        return UserTable.selectAll()
            .where { (UserTable.id eq userId) and (UserTable.deleted eq false) }
            .map { it.toUserResponse() }
            .singleOrNull()
    }

    /**
     * 查询用户
     */
    fun findByUsername(username: String): UserResponse? {
        return UserTable.selectAll()
            .where { (UserTable.username eq username) and (UserTable.deleted eq false) }
            .map { it.toUserResponse() }
            .singleOrNull()
    }

    /**
     * 创建用户 (DSL)
     * 使用 insertAndGetId 直接插入并获取生成的 ID
     */
    fun createUser(req: UserRegister, encodedPassword: String): Long {
        val insertedId = UserTable.insertAndGetId {
            it[username] = req.username
            it[password] = encodedPassword
        }

        // 返回 Long 类型的 ID 值
        return insertedId.value
    }

    fun getPassword(username: String): String? {
        return UserTable.select(UserTable.password)
            .where { (UserTable.username eq username) and (UserTable.deleted eq false) }
            .map { it[UserTable.password] }
            .singleOrNull()
    }

    fun updateLastLoginTime(userId: Long) {
        UserTable.update({ (UserTable.id eq userId) and (UserTable.deleted eq false) }) {
            it[lastLoginTime] = LocalDateTime.now()
        }
    }

    fun softDeleteUser(userId: Long): Int {
        return UserTable.update({ (UserTable.id eq userId) and (UserTable.deleted eq false) }) {
            it[deleted] = true
            it[updateTime] = LocalDateTime.now()
        }
    }

    fun updateUserProfile(userId: Long, req: UserProfileUpdate): Int {
        return UserTable.update({ (UserTable.id eq userId) and (UserTable.deleted eq false) }) {
            req.email?.let { email -> it[UserTable.email] = email }
            req.phone?.let { phone -> it[UserTable.phone] = phone }
            req.avatar?.let { avatar -> it[UserTable.avatar] = avatar }
            req.bio?.let { bio -> it[UserTable.bio] = bio }
            it[updateTime] = LocalDateTime.now()
        }
    }

    /**
     * 注释：将数据库结果行映射为 DTO 的扩展函数
     */
    private fun ResultRow.toUserResponse() = UserResponse(
        id = this[UserTable.id].value,
        username = this[UserTable.username],
        email = this[UserTable.email],
        avatar = this[UserTable.avatar],
        bio = this[UserTable.bio],
        status = this[UserTable.status],
        lastLoginTime = this[UserTable.lastLoginTime],
        createTime = this[UserTable.createTime]
    )
}
