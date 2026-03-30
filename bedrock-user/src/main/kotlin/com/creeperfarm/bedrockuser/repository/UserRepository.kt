package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.request.UserProfileUpdateRequest
import com.creeperfarm.bedrockuser.model.request.UserRegistrationRequest
import com.creeperfarm.bedrockuser.model.response.UserResponse
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import com.creeperfarm.bedrockuser.model.entity.UserTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserRepository {

    /** 查询单个有效用户。 */
    fun findByUserId(userId: Long): UserResponse? {
        return UserTable.selectAll()
            .where { (UserTable.id eq userId) and (UserTable.deleted eq false) }
            .map { it.toUserResponse() }
            .singleOrNull()
    }

    /** 按用户名查询有效用户。 */
    fun findByUsername(username: String): UserResponse? {
        return UserTable.selectAll()
            .where { (UserTable.username eq username) and (UserTable.deleted eq false) }
            .map { it.toUserResponse() }
            .singleOrNull()
    }

    /** 创建用户。 */
    fun createUser(request: UserRegistrationRequest, encodedPassword: String): Long {
        val insertedId = UserTable.insertAndGetId {
            it[username] = request.username
            it[password] = encodedPassword
            it[createTime] = LocalDateTime.now()
            it[updateTime] = LocalDateTime.now()
        }
        return insertedId.value
    }

    /** 查询登录校验所需的密码摘要。 */
    fun getPassword(username: String): String? {
        return UserTable.select(UserTable.password)
            .where { (UserTable.username eq username) and (UserTable.deleted eq false) }
            .map { it[UserTable.password] }
            .singleOrNull()
    }

    /** 更新最后登录时间。 */
    fun updateLastLoginTime(userId: Long) {
        UserTable.update({ (UserTable.id eq userId) and (UserTable.deleted eq false) }) {
            it[lastLoginTime] = LocalDateTime.now()
        }
    }

    /** 软删除用户。 */
    fun softDeleteUser(userId: Long): Int {
        return UserTable.update({ (UserTable.id eq userId) and (UserTable.deleted eq false) }) {
            it[deleted] = true
            it[updateTime] = LocalDateTime.now()
        }
    }

    /** 更新用户资料。 */
    fun updateUserProfile(userId: Long, request: UserProfileUpdateRequest): Int {
        return UserTable.update({ (UserTable.id eq userId) and (UserTable.deleted eq false) }) {
            request.email?.let { email -> it[UserTable.email] = email }
            request.phone?.let { phone -> it[UserTable.phone] = phone }
            request.avatar?.let { avatar -> it[UserTable.avatar] = avatar }
            request.bio?.let { bio -> it[UserTable.bio] = bio }
            it[updateTime] = LocalDateTime.now()
        }
    }

    /** 分页查询有效用户。 */
    fun findUsers(offset: Long, limit: Int, username: String?): List<UserResponse> {
        val query = UserTable.selectAll().where { UserTable.deleted eq false }

        if (!username.isNullOrBlank()) {
            query.andWhere { UserTable.username like "%$username%" }
        }

        return query.limit(limit)
            .offset(offset)
            .map { it.toUserResponse() }
    }

    /** 查询角色下的有效用户。 */
    fun findUsersByRoleId(offset: Long, limit: Int, roleId: Long): List<UserResponse> {
        return (UserTable innerJoin UserRoleTable)
            .selectAll()
            .where { (UserRoleTable.roleId eq roleId) and (UserTable.deleted eq false) }
            .limit(limit)
            .offset(offset)
            .map { it.toUserResponse() }
    }

    /** 映射用户查询结果。 */
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
