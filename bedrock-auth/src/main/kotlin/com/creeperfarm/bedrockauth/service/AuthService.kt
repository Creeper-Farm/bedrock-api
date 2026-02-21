package com.creeperfarm.bedrockauth.service

import com.creeperfarm.bedrockauth.model.dto.TokenResponse
import com.creeperfarm.bedrockauth.utils.JwtUtils
import com.creeperfarm.bedrockuser.model.dto.UserRegister
import com.creeperfarm.bedrockuser.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val redisTemplate: StringRedisTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 用户登录逻辑
     */
    @Transactional
    fun login(req: UserRegister): TokenResponse {
        log.info("User login attempt: {}", req.username)

        // 获取用户实体和加密后的密码
        val user = userRepository.findByUsername(req.username) ?: throw RuntimeException("User not found")
        val encodedPassword = userRepository.getPassword(req.username) ?: throw RuntimeException("Internal error")

        // 校验密码是否正确
        if (!passwordEncoder.matches(req.password, encodedPassword)) {
            log.warn("Login failed: Password mismatch for user '{}'", req.username)
            throw RuntimeException("Invalid credentials")
        }

        // 调用私有方法统一处理 Token 生成和 Redis 写入
        return generateAndStoreTokens(user.id, user.username)
    }

    /**
     * 刷新 Token 逻辑
     * 注释：校验传入的 refreshToken，通过后发放新的双 Token
     */
    @Transactional
    fun refreshToken(oldRefreshToken: String): TokenResponse {
        log.info("Attempting to refresh token")

        try {
            // 校验并解析旧的 RefreshToken
            val decodedJWT = jwtUtils.decodeToken(oldRefreshToken)
            val userId = decodedJWT.subject.toLong()
            val username = decodedJWT.getClaim("username").asString()

            // 检查 Redis 中是否存在且匹配 (安全检查)
            val redisKey = "auth:token:refresh:$userId"
            val savedToken = redisTemplate.opsForValue().get(redisKey)

            if (savedToken == null || savedToken != oldRefreshToken) {
                log.warn("Refresh token mismatch or expired in Redis for userId: {}", userId)
                throw RuntimeException("Refresh token is invalid or has expired")
            }

            // 生成新的双 Token 并更新 Redis
            log.info("Refresh token validated for user: {}, issuing new tokens", username)
            return generateAndStoreTokens(userId, username)

        } catch (e: Exception) {
            log.error("Token refresh failed: {}", e.message)
            throw RuntimeException("Token refresh failed: ${e.message}")
        }
    }

    /**
     * 私有辅助方法：生成双 Token 并同步到 Redis
     * 注释：复用登录和刷新的核心逻辑
     */
    private fun generateAndStoreTokens(userId: Long, username: String): TokenResponse {
        val at = jwtUtils.createAccessToken(userId, username)
        val rt = jwtUtils.createRefreshToken(userId, username)

        // 存入 Redis，单位使用秒 (SECONDS)
        redisTemplate.opsForValue().set(
            "auth:token:access:$userId", at, jwtUtils.accessTokenExp, TimeUnit.SECONDS
        )
        redisTemplate.opsForValue().set(
            "auth:token:refresh:$userId", rt, jwtUtils.refreshTokenExp, TimeUnit.SECONDS
        )

        return TokenResponse(at, rt, jwtUtils.accessTokenExp, jwtUtils.refreshTokenExp)
    }

    /**
     * 注销登录态，删除 Redis 中保存的 token
     */
    @Transactional
    fun logout(userId: Long) {
        val deleted = redisTemplate.delete(
            listOf("auth:token:access:$userId", "auth:token:refresh:$userId")
        ) ?: 0L
        log.info("User logout completed, userId: {}, deleted token keys: {}", userId, deleted)
    }
}
