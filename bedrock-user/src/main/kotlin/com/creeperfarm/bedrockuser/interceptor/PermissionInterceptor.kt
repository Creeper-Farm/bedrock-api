package com.creeperfarm.bedrockuser.interceptor

import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.enums.Logical
import com.creeperfarm.bedrockuser.repository.PermissionRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class PermissionInterceptor(private val permissionRepository: PermissionRepository) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // 1. 仅拦截 Controller 方法
        if (handler !is HandlerMethod) return true

        // 2. 获取权限注解
        val annotation = handler.getMethodAnnotation(RequiresPermissions::class.java) ?: return true

        // 3. 获取当前用户 ID (由 JwtInterceptor 存入)
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw RuntimeException("Authentication required") // 401 逻辑

        // 4. 执行权限比对 (必须包裹在 transaction 中解决 No transaction 报错)
        val hasPermission = transaction {
            // 从数据库查询该用户拥有的所有权限字符 (code)
            val ownedPermissionCodes = permissionRepository.findByUserId(userId).map { it.code }.toSet()

            val requiredPermissions = annotation.value

            if (annotation.logical == Logical.AND) {
                // 全部包含
                requiredPermissions.all { ownedPermissionCodes.contains(it) }
            } else {
                // 包含其一
                requiredPermissions.any { ownedPermissionCodes.contains(it) }
            }
        }

        if (!hasPermission) {
            log.warn("User {} missing permissions: {}", userId, annotation.value.joinToString())
            // 抛出异常后由 GlobalExceptionHandler 统一转为 403 错误
            throw RuntimeException("Forbidden: Access denied")
        }

        return true
    }
}