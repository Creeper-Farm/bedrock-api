package com.creeperfarm.bedrockauth.service

import com.creeperfarm.bedrockauth.model.request.LoginRequest
import com.creeperfarm.bedrockauth.model.response.TokenResponse
import com.creeperfarm.bedrockauth.utils.JwtUtils
import com.creeperfarm.bedrockuser.repository.UserRepository
import com.creeperfarm.bedrockuser.service.PermissionService
import com.creeperfarm.bedrockuser.service.UserDeviceService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userDeviceService: UserDeviceService,
    private val permissionService: PermissionService,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val redisTemplate: StringRedisTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 完成账号密码登录并签发 token。 */
    @Transactional
    fun login(request: LoginRequest, servletRequest: HttpServletRequest): TokenResponse {
        log.info("User login attempt: {}", request.username)

        val user = userRepository.findByUsername(request.username) ?: throw IllegalArgumentException("Invalid username or password")
        val encodedPassword = userRepository.getPassword(request.username)
            ?: throw IllegalArgumentException("Database integrity error: Password missing")

        if (!passwordEncoder.matches(request.password, encodedPassword)) {
            log.warn("Login failed: Password mismatch for user '{}'", request.username)
            throw IllegalArgumentException("Invalid username or password")
        }

        userRepository.updateLastLoginTime(user.id)
        userDeviceService.recordLoginDevice(
            userId = user.id,
            request = servletRequest,
            ipAddress = resolveClientIp(servletRequest)
        )

        val permissions = permissionService.getUserPermissions(user.id).map { it.code }
        return generateAndStoreTokens(user.id, user.username, permissions)
    }

    /** 校验 refresh token 并重新签发 token。 */
    @Transactional
    fun refreshToken(oldRefreshToken: String): TokenResponse {
        log.info("Attempting to refresh token")

        try {
            val decodedJWT = jwtUtils.decodeToken(oldRefreshToken)
            val userId = decodedJWT.subject.toLong()
            val username = decodedJWT.getClaim("username").asString()

            val user = userRepository.findByUserId(userId)
            if (user == null) {
                redisTemplate.delete(listOf("auth:token:access:$userId", "auth:token:refresh:$userId"))
                throw RuntimeException("Account not available")
            }

            val redisKey = "auth:token:refresh:$userId"
            val savedToken = redisTemplate.opsForValue().get(redisKey)

            if (savedToken == null || savedToken != oldRefreshToken) {
                log.warn("Refresh token mismatch or expired in Redis for userId: {}", userId)
                throw RuntimeException("Refresh token is invalid or has expired")
            }

            val permissions = permissionService.getUserPermissions(userId).map { it.code }
            log.info("Refresh token validated for user: {}, issuing new tokens", username)
            return generateAndStoreTokens(userId, username, permissions)

        } catch (e: Exception) {
            log.error("Token refresh failed: {}", e.message)
            throw RuntimeException("Token refresh failed: ${e.message}")
        }
    }

    /** 复用登录和刷新共用的发 token 逻辑。 */
    private fun generateAndStoreTokens(userId: Long, username: String, permissions: List<String>): TokenResponse {
        val at = jwtUtils.createAccessToken(userId, username, permissions)
        val rt = jwtUtils.createRefreshToken(userId, username)

        redisTemplate.opsForValue().set(
            "auth:token:access:$userId", at, jwtUtils.accessTokenExp, TimeUnit.SECONDS
        )
        redisTemplate.opsForValue().set(
            "auth:token:refresh:$userId", rt, jwtUtils.refreshTokenExp, TimeUnit.SECONDS
        )

        return TokenResponse(at, rt, jwtUtils.accessTokenExp, jwtUtils.refreshTokenExp)
    }

    /** 删除当前用户在 Redis 中的登录态。 */
    @Transactional
    fun logout(userId: Long) {
        val deleted = redisTemplate.delete(
            listOf("auth:token:access:$userId", "auth:token:refresh:$userId")
        ) ?: 0L
        log.info("User logout completed, userId: {}, deleted token keys: {}", userId, deleted)
    }

    private fun resolveClientIp(request: HttpServletRequest): String? {
        // 兼容反向代理头，优先取真实来源 IP。
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrBlank()) {
            return xForwardedFor.split(",").firstOrNull()?.trim()
        }
        return request.getHeader("X-Real-IP")?.takeIf { it.isNotBlank() } ?: request.remoteAddr
    }
}
