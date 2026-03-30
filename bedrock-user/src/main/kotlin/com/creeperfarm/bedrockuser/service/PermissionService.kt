package com.creeperfarm.bedrockuser.service

import com.creeperfarm.bedrockuser.model.dto.PermissionResponse
import com.creeperfarm.bedrockuser.model.enums.PermissionType
import com.creeperfarm.bedrockuser.repository.PermissionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {
    // 定义日志对象
    private val logger = LoggerFactory.getLogger(PermissionService::class.java)

    /**
     * 获取用户权限
     */
    @Transactional(readOnly = true)
    fun getUserPermissions(userId: Long): List<PermissionResponse> {
        logger.info("Getting user permissions for user {}", userId)
        return permissionRepository.findByUserId(userId)
    }

    /**
     * 分页查询权限列表
     */
    @Transactional(readOnly = true)
    fun getPermissions(page: Int, size: Int, name: String?): List<PermissionResponse> {
        logger.info("Getting permission list with pagination - Page: $page, Size: $size, Username: $name")
        val offset = ((page - 1) * size).toLong()
        return permissionRepository.findPermissionsPaged(offset, size, name)
    }

    /**
     * 获取查询权限总量
     */
    @Transactional(readOnly = true)
    fun getSearchPermissionTotal(name: String?): Long {
        logger.info("Getting search permission total - Search: $name")
        return permissionRepository.countActivePermissions(name)
    }

    /**
     * 创建权限
     */
    @Transactional
    fun createPermission(name: String, code: String, type: PermissionType): Long {
        logger.info("Creating new permission - Permission code: $code, type: $type")
        return permissionRepository.createPermission(name, code, type)
    }

    /**
     * 更新权限
     */
    @Transactional
    fun updatePermission(permissionId: Long, name: String, code: String, type: PermissionType): Boolean {
        logger.info("Updating permission - Permission code: $code, type: $type")
        return permissionRepository.updatePermission(permissionId, name, code, type)
    }

    /**
     * 删除权限
     */
    @Transactional
    fun deletePermission(permissionId: Long): Boolean {
        logger.info("Physically deleting permission - Permission id: {}", permissionId)
        return permissionRepository.deletePermission(permissionId)
    }
}
