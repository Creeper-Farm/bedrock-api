package com.creeperfarm.bedrockuser.repository

import com.creeperfarm.bedrockuser.model.dto.PermissionResponse
import com.creeperfarm.bedrockuser.model.entity.PermissionTable
import com.creeperfarm.bedrockuser.model.entity.RolePermissionTable
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class PermissionRepository {

    /**
     * 查询用户权限
     */
    fun findByUserId(userId: Long): List<PermissionResponse> {
        return (UserRoleTable innerJoin RoleTable
                innerJoin RolePermissionTable
                innerJoin PermissionTable).selectAll()
            .where { UserRoleTable.userId eq userId }
            .andWhere { PermissionTable.deleted eq false }
            .andWhere { RoleTable.deleted eq false }
            .map { it.toPermissionResponse() }
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