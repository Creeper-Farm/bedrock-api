package com.creeperfarm.bedrockuser.interceptor

import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.enums.PermissionMatchMode
import com.creeperfarm.bedrockuser.repository.PermissionRepository
import com.creeperfarm.bedrockuser.repository.RoleRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.nio.file.AccessDeniedException

@Component
class PermissionInterceptor(
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true

        val annotation = handler.getMethodAnnotation(RequiresPermissions::class.java) ?: return true

        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw RuntimeException("Authentication required")

        // 权限校验依赖 Exposed 查询，需显式包裹事务。
        val hasPermission = transaction {
            if (roleRepository.isSuperAdmin(userId)) {
                return@transaction true
            }

            val ownedPermissionCodes = permissionRepository.findPermissionsByUserId(userId).map { it.code }.toSet()

            val requiredPermissions = annotation.value

            if (annotation.matchMode == PermissionMatchMode.AND) {
                requiredPermissions.all { ownedPermissionCodes.contains(it) }
            } else {
                requiredPermissions.any { ownedPermissionCodes.contains(it) }
            }
        }

        if (!hasPermission) {
            log.warn("User {} missing permissions: {}", userId, annotation.value.joinToString())
            throw AccessDeniedException("Forbidden: You do not have permission to access this resource")
        }

        return true
    }
}
