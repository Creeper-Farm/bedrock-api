package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.dto.PermissionResponse
import com.creeperfarm.bedrockuser.model.entity.PermissionTable
import com.creeperfarm.bedrockuser.model.entity.RolePermissionTable
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import com.creeperfarm.bedrockuser.model.enums.PermissionType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.*
import org.springframework.stereotype.Repository

@Repository
class PermissionRepository {

    /**
     * 查询用户权限 (去重)
     */
    fun findByUserId(userId: Long): List<PermissionResponse> {
        return (UserRoleTable innerJoin RoleTable
                innerJoin RolePermissionTable
                innerJoin PermissionTable)
            .selectAll()
            .where { UserRoleTable.userId eq userId }
            .andWhere { RoleTable.deleted eq false }
            .map { it.toPermissionResponse() }
            .distinctBy { it.code }
    }

    /**
     * 分页查询权限
     */
    fun findPermissionsPaged(offset: Long, limit: Int, name: String?): List<PermissionResponse> {
        val query = PermissionTable.selectAll()

        if (!name.isNullOrBlank()) {
            query.andWhere { PermissionTable.name like "%$name%" }
        }

        return query.orderBy(PermissionTable.createTime to SortOrder.DESC)
            .limit(limit)
            .offset(offset)
            .map { it.toPermissionResponse() }
    }

    /**
     * 查询权限总数
     */
    fun countActivePermissions(name: String?): Long {
        val query = PermissionTable.selectAll()
        if (!name.isNullOrBlank()) {
            query.andWhere { PermissionTable.name like "%$name%" }
        }
        return query.count()
    }

    /**
     * 根据权限 ID 列表统计存在的权限数量
     */
    fun countPermissionsByIds(permissionIds: List<Long>): Long {
        if (permissionIds.isEmpty()) {
            return 0
        }

        return PermissionTable
            .select(PermissionTable.id)
            .where { PermissionTable.id inList permissionIds }
            .count()
    }

    /**
     * 根据角色 ID 查询拥有的权限 ID 列表
     * 注释：用于编辑角色时，在前端勾选已有的权限
     */
    fun findIdsByRoleId(roleId: Long): List<Long> {
        return RolePermissionTable.selectAll()
            .where { RolePermissionTable.roleId eq roleId }
            .map { it[RolePermissionTable.permissionId].value }
    }

    /**
     * 创建权限
     */
    fun createPermission(name: String, code: String, type: PermissionType): Long {
        val insertId = PermissionTable.insertAndGetId {
            it[PermissionTable.name] = name
            it[PermissionTable.code] = code
            it[PermissionTable.type] = type
        }
        return insertId.value
    }

    /**
     * 更新权限
     */
    fun updatePermission(permissionId: Long, name: String, code: String, type: PermissionType): Boolean {
        val affectedRows = PermissionTable.update({ PermissionTable.id eq permissionId }) {
            it[PermissionTable.name] = name
            it[PermissionTable.code] = code
            it[PermissionTable.type] = type
        }
        return affectedRows == 1
    }

    /**
     * 物理删除权限并清理关联关系
     */
    fun deletePermission(permissionId: Long): Boolean {
        // 清理角色-权限关联表
        RolePermissionTable.deleteWhere { RolePermissionTable.permissionId eq permissionId }
        // 物理删除权限表记录
        val affectedRows = PermissionTable.deleteWhere { PermissionTable.id eq permissionId }
        return affectedRows == 1
    }

    /**
     * 将数据库行为映射为 DTO 的扩展函数
     */
    private fun ResultRow.toPermissionResponse() = PermissionResponse(
        id = this[PermissionTable.id].value,
        name = this[PermissionTable.name],
        code = this[PermissionTable.code],
        createTime = this[PermissionTable.createTime],
        updateTime = this[PermissionTable.updateTime]
    )
}
