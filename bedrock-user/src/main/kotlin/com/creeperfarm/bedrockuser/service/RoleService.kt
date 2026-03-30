package com.creeperfarm.bedrockuser.service

import com.creeperfarm.bedrockuser.model.response.RoleResponse
import com.creeperfarm.bedrockuser.repository.PermissionRepository
import com.creeperfarm.bedrockuser.repository.RoleRepository
import com.creeperfarm.bedrockuser.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository
) {
    private val logger = LoggerFactory.getLogger(RoleService::class.java)

    /**
     * 分页获取角色列表
     */
    @Transactional(readOnly = true)
    fun listRoles(page: Int, size: Int, name: String?): List<RoleResponse> {
        logger.info("Fetching role list with pagination - Page: $page, Size $size")
        val offset = ((page - 1) * size).toLong()
        return roleRepository.findPagedRoles(offset, size, name)
    }

    /**
     * 获取用户角色列表
     */
    @Transactional(readOnly = true)
    fun listRolesByUserId(userId: Long): List<RoleResponse> {
        logger.info("Fetching role list with userId: $userId")
        return roleRepository.findRolesByUserId(userId)
    }

    /**
     * 更新用户角色绑定关系
     */
    @Transactional
    fun updateUserRoleAssignments(userId: Long, roleIds: List<Long>): Boolean {
        logger.info("Updating roles for userId: {}", userId)

        if (userRepository.findByUserId(userId) == null) {
            logger.warn("Update user roles failed: User '{}' not found or deleted", userId)
            throw RuntimeException("User not found")
        }

        val distinctRoleIds = roleIds.distinct()
        if (distinctRoleIds.any { it <= 0 }) {
            throw IllegalArgumentException("Role ID must be greater than 0")
        }

        val existingRoleCount = roleRepository.countActiveRolesByIds(distinctRoleIds)
        if (existingRoleCount != distinctRoleIds.size.toLong()) {
            logger.warn("Update user roles failed: Some roles do not exist or are deleted, userId: {}", userId)
            throw RuntimeException("Some roles do not exist")
        }

        return roleRepository.replaceUserRoles(userId, distinctRoleIds)
    }

    /**
     * 为角色重新绑定权限列表
     */
    @Transactional
    fun updateRolePermissionAssignments(roleId: Long, permissionIds: List<Long>): Boolean {
        logger.info("Updating permissions for roleId: {}", roleId)

        if (roleRepository.countActiveRolesByIds(listOf(roleId)) != 1L) {
            logger.warn("Update role permissions failed: Role '{}' not found or deleted", roleId)
            throw RuntimeException("Role not found")
        }

        val distinctPermissionIds = permissionIds.distinct()
        if (distinctPermissionIds.any { it <= 0 }) {
            throw IllegalArgumentException("Permission ID must be greater than 0")
        }

        val existingPermissionCount = permissionRepository.countPermissionsByIds(distinctPermissionIds)
        if (existingPermissionCount != distinctPermissionIds.size.toLong()) {
            logger.warn("Update role permissions failed: Some permissions do not exist, roleId: {}", roleId)
            throw RuntimeException("Some permissions do not exist")
        }

        return roleRepository.replaceRolePermissions(roleId, distinctPermissionIds)
    }

}
