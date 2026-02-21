package com.creeperfarm.bedrockauth.interceptor

import com.creeperfarm.bedrockauth.utils.JwtUtils
import jakarta.security.auth.message.AuthException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class JwtInterceptor(
    private val jwtUtils: JwtUtils,
    private val redisTemplate: StringRedisTemplate
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authHeader = request.getHeader("Authorization")

        // 校验 Header 是否存在
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for URI: {}", request.requestURI)
            throw AuthException("Missing or invalid token")
        }

        val token = authHeader.substring(7)

        try {
            // 校验 JWT 签名（这一步很快，不费资源）
            val decodedJWT = jwtUtils.decodeToken(token)
            val userId = decodedJWT.subject

            // 校验 Redis 状态（确保 Token 未被注销或拉黑）
            val redisKey = "auth:token:access:$userId"
            val savedToken = redisTemplate.opsForValue().get(redisKey)

            if (savedToken == null) {
                log.info("Token expired in Redis for userId: {}", userId)
                throw AuthException("Session expired, please login again")
            }

            if (savedToken != token) {
                log.warn("Token mismatch for userId: {}. Possible concurrent login or old token usage.", userId)
                throw AuthException("Token is no longer valid")
            }

            // 存入 Request 属性，方便下游 Controller 调用
            request.setAttribute("userId", userId)
            return true
        } catch (e: Exception) {
            log.error("JWT validation failed for URI {}: {}", request.requestURI, e.message)
            throw AuthException("Authentication failed: ${e.message}")
        }
    }
}