package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.dto.UserRegister
import com.creeperfarm.bedrockuser.model.dto.UserResponse
import com.creeperfarm.bedrockuser.model.entity.UserTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class UserRepository {

    /**
     * 查询用户 (DSL)
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