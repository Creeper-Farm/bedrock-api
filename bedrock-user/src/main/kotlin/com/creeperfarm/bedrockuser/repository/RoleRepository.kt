package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.dto.RoleResponse
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class RoleRepository {

    /**
     * 查询用户角色列表
     */
    fun findByUserId(userId: Long): List<RoleResponse> {
        return (UserRoleTable innerJoin RoleTable)
            .selectAll()
            .where {
                (UserRoleTable.userId eq userId) and (RoleTable.deleted eq false)
            }
            .map { it.toRoleResponse() }
    }

    /**
     * 分页查询角色列表
     */
    fun findRoles(offset: Long, limit: Int, name: String?): List<RoleResponse> {
        val query = RoleTable.selectAll().where { RoleTable.deleted eq false }

        if (name != null) {
            query.andWhere { RoleTable.name like "%$name%" }
        }

        return query.limit(limit)
            .offset(offset)
            .map { it.toRoleResponse() }
    }

    /**
     * 为用户绑定角色
     */
    fun assignRoleToUser(userId: Long, roleId: Long) {
        UserRoleTable.insert {
            it[UserRoleTable.userId] = userId
            it[UserRoleTable.roleId] = roleId
        }
    }

    /**
     * 删除用户的所有角色关联
     * 注释：通常在重新分配角色前，先清空旧的关系
     */
    fun deleteAllRolesByUserId(userId: Long): Int {
        return UserRoleTable.deleteWhere {
            UserRoleTable.userId eq userId
        }
    }

    /**
     * 批量为用户分配角色 (多对多)
     * 注释：先删除用户现有的所有角色关联，再批量插入新的关联
     */
    fun updateUserRoles(userId: Long, roleIds: List<Long>) {
        // 删除旧关联
        UserRoleTable.deleteWhere { UserRoleTable.userId eq userId }

        // 批量插入新关联
        roleIds.forEach { roleId ->
            UserRoleTable.insert {
                it[UserRoleTable.userId] = userId
                it[UserRoleTable.roleId] = roleId
            }
        }
    }

    /**
     * 统计有效角色数量
     */
    fun countActiveRolesByIds(roleIds: List<Long>): Long {
        if (roleIds.isEmpty()) {
            return 0
        }

        return RoleTable
            .select(RoleTable.id)
            .where {
                (RoleTable.id inList roleIds) and (RoleTable.deleted eq false)
            }
            .count()
    }

    /**
     * 快速检查用户是否拥有超级管理员角色
     */
    fun isSuperAdmin(userId: Long): Boolean {
        val query = (UserRoleTable innerJoin RoleTable)
            .select(RoleTable.id)
            .where {
                (UserRoleTable.userId eq userId) and
                        (RoleTable.code eq "SUPER_ADMIN") and
                        (RoleTable.deleted eq false)
            }
            .limit(1)

        return !query.empty()
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
