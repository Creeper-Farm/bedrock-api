package com.creeperfarm.bedrockauth.interceptor

import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockauth.utils.JwtUtils
import com.creeperfarm.bedrockuser.repository.UserRepository
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(
    private val jwtUtils: JwtUtils,
    private val redisTemplate: StringRedisTemplate,
    private val userRepository: UserRepository
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // 1. 如果请求不是指向具体的 Controller 方法（比如静态资源、404等），直接放行
        if (handler !is HandlerMethod) {
            return true
        }

        // 2. 检查方法上或所属的类上是否存在 @Authenticated 注解
        val hasAnnotation = handler.hasMethodAnnotation(Authenticated::class.java) ||
                handler.beanType.isAnnotationPresent(Authenticated::class.java)

        // 3. 如果没有该注解，说明是公开接口，直接放行
        if (!hasAnnotation) {
            return true
        }

        // --- 4. 以下为受保护接口的校验逻辑 ---
        val authHeader = request.getHeader("Authorization")

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            log.warn("Unauthorized access attempt at URI: {}", request.requestURI)
            throw AuthException("Missing or invalid token")
        }

        val token = authHeader.substring(7)

        try {
            val decodedJWT = jwtUtils.decodeToken(token)
            val userId = decodedJWT.subject
            val userIdLong = userId.toLongOrNull() ?: throw AuthException("Invalid token subject")

            // 校验 Redis 状态
            val redisKey = "auth:token:access:$userId"
            val savedToken = redisTemplate.opsForValue().get(redisKey)

            if (savedToken == null) {
                log.info("Token expired in Redis for userId: {}", userId)
                throw AuthException("Session expired, please login again")
            }

            if (savedToken != token) {
                log.warn("Token mismatch for userId: {}. Security risk suspected.", userId)
                throw AuthException("Token is no longer valid")
            }

            // 校验用户可用性
            val user = userRepository.findByUserId(userIdLong)
            if (user == null) {
                redisTemplate.delete(listOf("auth:token:access:$userId", "auth:token:refresh:$userId"))
                log.warn("Access denied: User account {} no longer exists", userId)
                throw AuthException("Account not available")
            }

            // 解析权限列表并存入 Request 域
            val permissions = decodedJWT.getClaim("permissions").asList(String::class.java) ?: emptyList()

            request.setAttribute("userId", userId)
            request.setAttribute("userPermissions", permissions) // 存入权限以便后续校验

            return true
        } catch (e: Exception) {
            log.error("JWT validation failed for URI {}: {}", request.requestURI, e.message)
            throw AuthException("Authentication failed: ${e.message}")
        }
    }
}