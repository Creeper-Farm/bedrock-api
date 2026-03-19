package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.dto.RoleResponse
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class RoleRepository {

    /**
     * 查询用户角色列表
     */
    fun findById(userId: Long): List<RoleResponse> {
        return (UserRoleTable innerJoin RoleTable)
            .selectAll()
            .where {
                (UserRoleTable.userId eq userId) and (RoleTable.deleted eq false)
            }
            .map { it.toRoleResponse() }
    }

    /**
     * 快速检查用户是否拥有超级管理员角色
     * 注释：只要查到一条匹配记录即返回 true，性能更高
     */
    fun isSuperAdmin(userId: Long): Boolean {
        return (UserRoleTable innerJoin RoleTable)
            .select(RoleTable.code)
            .where {
                (UserRoleTable.userId eq userId) and (RoleTable.code eq "SUPER_ADMIN") and (RoleTable.deleted eq false)
            }
            .limit(1)
            .count() > 0
    }

    /**
     * 将数据库行为映射为 DTO 的扩展函数
     */
    private fun ResultRow.toRoleResponse() = RoleResponse(
        id = this[RoleTable.id].value,
        name = this[RoleTable.name],
        code = this[RoleTable.code],
        createTime = this[RoleTable.createTime],
        updateTime = this[RoleTable.updateTime]
    )

}