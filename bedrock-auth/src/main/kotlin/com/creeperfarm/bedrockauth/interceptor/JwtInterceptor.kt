package com.creeperfarm.bedrockauth.interceptor

import com.creeperfarm.bedrockauth.utils.JwtUtils
import jakarta.security.auth.message.AuthException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.transaction.annotation.Transactional

@Component
class JwtInterceptor(
    private val jwtUtils: JwtUtils,
    private val redisTemplate: StringRedisTemplate
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // 提取 Header
        val authHeader = request.getHeader("Authorization")

        // 2. 校验 Header 格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for URI: {}", request.requestURI)
            // 直接抛出异常，触发 GlobalExceptionHandler
            throw AuthException("Missing or invalid token")
        }

        val token = authHeader.substring(7)

        try {
            // 校验 JWT 签名和过期时间
            val decodedJWT = jwtUtils.decodeToken(token)
            val userId = decodedJWT.subject

            // 校验 Redis 状态
            val redisKey = "auth:token:access:$userId"
            val savedToken = redisTemplate.opsForValue().get(redisKey)

            if (savedToken == null || savedToken != token) {
                log.warn("Token mismatch or expired in Redis for userId: {}", userId)
                // 抛出异常，说明 Token 已失效
                throw AuthException("Token has expired or is invalid")
            }

            // 存入 Request 属性
            request.setAttribute("userId", userId)
            return true
        } catch (e: Exception) {
            log.error("JWT validation failed: {}", e.message)
            // 捕获所有 JWT 解析错误（如过期、伪造），统一转成 AuthException
            // 这样 GlobalExceptionHandler 就能统一返回 401
            throw AuthException(e.message ?: "Invalid Token")
        }
    }
}