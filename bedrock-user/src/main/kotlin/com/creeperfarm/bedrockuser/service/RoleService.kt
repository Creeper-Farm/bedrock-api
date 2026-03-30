package com.creeperfarm.bedrockuser.service

import com.creeperfarm.bedrockuser.model.response.RoleResponse
import com.creeperfarm.bedrockuser.repository.PermissionRepository
import com.creeperfarm.bedrockuser.repository.RoleRepository
import com.creeperfarm.bedrockuser.repository.UserRepository
import com.creeperfarm.bedrockcommon.web.pageQuery
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

    /** 分页查询角色列表。 */
    @Transactional(readOnly = true)
    fun listRoles(page: Int, size: Int, name: String?): List<RoleResponse> {
        logger.info("Fetching role list with pagination - Page: $page, Size $size")
        val query = pageQuery(page, size)
        return roleRepository.findPagedRoles(query.offset, query.size, name)
    }

    /** 统计角色列表数量。 */
    @Transactional(readOnly = true)
    fun countRoles(name: String?): Long {
        return roleRepository.countRoles(name)
    }

    /** 查询用户角色列表。 */
    @Transactional(readOnly = true)
    fun listRolesByUserId(userId: Long): List<RoleResponse> {
        logger.info("Fetching role list with userId: $userId")
        return roleRepository.findRolesByUserId(userId)
    }

    /** 覆盖用户角色绑定。 */
    @Transactional
    fun updateUserRoleAssignments(userId: Long, roleIds: List<Long>): Boolean {
        logger.info("Updating roles for userId: {}", userId)

        requireUser(userId, "Update user roles")
        val distinctRoleIds = sanitizeIds(roleIds, "Role")
        requireRoles(distinctRoleIds, userId, "Update user roles")
        return roleRepository.replaceUserRoles(userId, distinctRoleIds)
    }

    /** 为用户追加单个角色绑定。 */
    @Transactional
    fun addUserRoleAssignment(userId: Long, roleId: Long): Boolean {
        logger.info("Adding role {} for userId: {}", roleId, userId)
        requireUser(userId, "Add user role")

        val validRoleId = sanitizeIds(listOf(roleId), "Role").single()
        requireRoles(listOf(validRoleId), userId, "Add user role")
        if (roleRepository.hasUserRole(userId, validRoleId)) {
            return true
        }

        return roleRepository.assignRoleToUser(userId, validRoleId)
    }

    /** 覆盖角色权限绑定。 */
    @Transactional
    fun updateRolePermissionAssignments(roleId: Long, permissionIds: List<Long>): Boolean {
        logger.info("Updating permissions for roleId: {}", roleId)

        requireRole(roleId, "Update role permissions")
        val distinctPermissionIds = sanitizeIds(permissionIds, "Permission")
        requirePermissions(distinctPermissionIds, roleId, "Update role permissions")
        return roleRepository.replaceRolePermissions(roleId, distinctPermissionIds)
    }

    /** 为角色追加单个权限绑定。 */
    @Transactional
    fun addRolePermissionAssignment(roleId: Long, permissionId: Long): Boolean {
        logger.info("Adding permission {} for roleId: {}", permissionId, roleId)
        requireRole(roleId, "Add role permission")

        val validPermissionId = sanitizeIds(listOf(permissionId), "Permission").single()
        requirePermissions(listOf(validPermissionId), roleId, "Add role permission")
        if (roleRepository.hasRolePermission(roleId, validPermissionId)) {
            return true
        }

        return roleRepository.assignPermissionToRole(roleId, validPermissionId)
    }

    private fun sanitizeIds(ids: List<Long>, subject: String): List<Long> {
        val distinctIds = ids.distinct()
        if (distinctIds.any { it <= 0 }) {
            throw IllegalArgumentException("$subject ID must be greater than 0")
        }
        return distinctIds
    }

    private fun requireUser(userId: Long, operation: String) {
        if (userRepository.findByUserId(userId) == null) {
            logger.warn("{} failed: User '{}' not found or deleted", operation, userId)
            throw RuntimeException("User not found")
        }
    }

    private fun requireRole(roleId: Long, operation: String) {
        if (roleRepository.countActiveRolesByIds(listOf(roleId)) != 1L) {
            logger.warn("{} failed: Role '{}' not found or deleted", operation, roleId)
            throw RuntimeException("Role not found")
        }
    }

    private fun requireRoles(roleIds: List<Long>, userId: Long, operation: String) {
        val existingRoleCount = roleRepository.countActiveRolesByIds(roleIds)
        if (existingRoleCount != roleIds.size.toLong()) {
            logger.warn("{} failed: Some roles do not exist or are deleted, userId: {}", operation, userId)
            throw RuntimeException("Some roles do not exist")
        }
    }

    private fun requirePermissions(permissionIds: List<Long>, roleId: Long, operation: String) {
        val existingPermissionCount = permissionRepository.countPermissionsByIds(permissionIds)
        if (existingPermissionCount != permissionIds.size.toLong()) {
            logger.warn("{} failed: Some permissions do not exist, roleId: {}", operation, roleId)
            throw RuntimeException("Some permissions do not exist")
        }
    }
}
