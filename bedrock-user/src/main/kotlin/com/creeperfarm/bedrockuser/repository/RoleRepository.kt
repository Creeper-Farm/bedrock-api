package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.response.RoleResponse
import com.creeperfarm.bedrockuser.model.entity.RolePermissionTable
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class RoleRepository {

    /** 查询用户角色列表。 */
    fun findRolesByUserId(userId: Long): List<RoleResponse> {
        return (UserRoleTable innerJoin RoleTable)
            .selectAll()
            .where {
                (UserRoleTable.userId eq userId) and (RoleTable.deleted eq false)
            }
            .map { it.toRoleResponse() }
    }

    /** 分页查询角色列表。 */
    fun findPagedRoles(offset: Long, limit: Int, name: String?): List<RoleResponse> {
        val query = buildActiveRoleQuery(name)

        return query.limit(limit)
            .offset(offset)
            .map { it.toRoleResponse() }
    }

    /** 统计有效角色数量。 */
    fun countRoles(name: String?): Long {
        return buildActiveRoleQuery(name).count()
    }

    /** 为用户追加单个角色。 */
    fun assignRoleToUser(userId: Long, roleId: Long): Boolean {
        return UserRoleTable.insert {
            it[UserRoleTable.userId] = userId
            it[UserRoleTable.roleId] = roleId
        }.insertedCount == 1
    }

    /** 为角色追加单个权限。 */
    fun assignPermissionToRole(roleId: Long, permissionId: Long): Boolean {
        return RolePermissionTable.insert {
            it[RolePermissionTable.roleId] = roleId
            it[RolePermissionTable.permissionId] = permissionId
        }.insertedCount == 1
    }

    /** 覆盖用户的角色关联。 */
    fun replaceUserRoles(userId: Long, roleIds: List<Long>): Boolean {
        UserRoleTable.deleteWhere { UserRoleTable.userId eq userId }
        return insertUserRoles(userId, roleIds)
    }

    /** 覆盖角色的权限关联。 */
    fun replaceRolePermissions(roleId: Long, permissionIds: List<Long>): Boolean {
        RolePermissionTable.deleteWhere { RolePermissionTable.roleId eq roleId }
        return insertRolePermissions(roleId, permissionIds)
    }

    /** 查询给定 ID 中存在的有效角色数量。 */
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

    /** 判断用户是否已绑定指定角色。 */
    fun hasUserRole(userId: Long, roleId: Long): Boolean {
        return UserRoleTable
            .select(UserRoleTable.id)
            .where { (UserRoleTable.userId eq userId) and (UserRoleTable.roleId eq roleId) }
            .limit(1)
            .any()
    }

    /** 判断角色是否已绑定指定权限。 */
    fun hasRolePermission(roleId: Long, permissionId: Long): Boolean {
        return RolePermissionTable
            .select(RolePermissionTable.id)
            .where { (RolePermissionTable.roleId eq roleId) and (RolePermissionTable.permissionId eq permissionId) }
            .limit(1)
            .any()
    }

    /** 判断用户是否拥有超级管理员角色。 */
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

    /** 映射角色查询结果。 */
    private fun ResultRow.toRoleResponse() = RoleResponse(
        id = this[RoleTable.id].value,
        name = this[RoleTable.name],
        code = this[RoleTable.code],
        createTime = this[RoleTable.createTime],
        updateTime = this[RoleTable.updateTime]
    )

    private fun buildActiveRoleQuery(name: String?): Query {
        val query = RoleTable.selectAll().where { RoleTable.deleted eq false }
        if (!name.isNullOrBlank()) {
            query.andWhere { RoleTable.name like "%$name%" }
        }
        return query
    }

    private fun insertUserRoles(userId: Long, roleIds: List<Long>): Boolean {
        if (roleIds.isEmpty()) {
            return true
        }

        val insertedRows = UserRoleTable.batchInsert(roleIds) { roleId ->
            this[UserRoleTable.userId] = userId
            this[UserRoleTable.roleId] = roleId
        }
        return insertedRows.size == roleIds.size
    }

    private fun insertRolePermissions(roleId: Long, permissionIds: List<Long>): Boolean {
        if (permissionIds.isEmpty()) {
            return true
        }

        val insertedRows = RolePermissionTable.batchInsert(permissionIds) { permissionId ->
            this[RolePermissionTable.roleId] = roleId
            this[RolePermissionTable.permissionId] = permissionId
        }
        return insertedRows.size == permissionIds.size
    }
}
