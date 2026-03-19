package com.creeperfarm.bedrockauth.utils

import com.creeperfarm.bedrockauth.config.AuthJwtProperties
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils(
    private val jwtProperties: AuthJwtProperties
) {
    private val algorithm by lazy { Algorithm.HMAC256(jwtProperties.secret) }

    // 有效期定义
    val accessTokenExp: Long
        get() = jwtProperties.accessTokenExpSeconds
    val refreshTokenExp: Long
        get() = jwtProperties.refreshTokenExpSeconds

    /**
     * 创建 AccessToken，包含用户权限信息
     */
    fun createAccessToken(userId: Long, username: String, permissions: List<String>): String =
        generate(userId, username, accessTokenExp, permissions)

    /**
     * 创建 RefreshToken，通常不携带权限信息以保持轻量
     */
    fun createRefreshToken(userId: Long, username: String): String =
        generate(userId, username, refreshTokenExp)

    /**
     * 统一生成 Token 的逻辑
     * @param permissions 权限代码列表，默认为空
     */
    private fun generate(
        userId: Long,
        username: String,
        seconds: Long,
        permissions: List<String> = emptyList()
    ): String {
        val builder = JWT.create()
            .withSubject(userId.toString())
            .withClaim("username", username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + seconds * 1000))

        // 如果权限列表不为空，则存入 Claim
        if (permissions.isNotEmpty()) {
            builder.withArrayClaim("permissions", permissions.toTypedArray())
        }

        return builder.sign(algorithm)
    }

    fun decodeToken(token: String): DecodedJWT {
        return JWT.require(algorithm).build().verify(token)
    }
}