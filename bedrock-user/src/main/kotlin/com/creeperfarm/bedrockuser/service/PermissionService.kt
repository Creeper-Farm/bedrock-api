package com.creeperfarm.bedrockuser.service

import com.creeperfarm.bedrockuser.model.dto.PermissionResponse
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
}