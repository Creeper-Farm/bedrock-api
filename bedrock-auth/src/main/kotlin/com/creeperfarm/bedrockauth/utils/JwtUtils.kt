package com.creeperfarm.bedrockauth.utils

import com.creeperfarm.bedrockauth.config.JwtTokenProperties
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils(
    private val jwtProperties: JwtTokenProperties
) {
    private val algorithm by lazy { Algorithm.HMAC256(jwtProperties.secret) }

    val accessTokenExp: Long
        get() = jwtProperties.accessTokenExpSeconds
    val refreshTokenExp: Long
        get() = jwtProperties.refreshTokenExpSeconds

    /** 创建携带权限声明的 access token。 */
    fun createAccessToken(userId: Long, username: String, permissions: List<String>): String =
        generate(userId, username, accessTokenExp, permissions)

    /** 创建 refresh token。 */
    fun createRefreshToken(userId: Long, username: String): String =
        generate(userId, username, refreshTokenExp)

    /** 统一的 token 构造逻辑。 */
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

        if (permissions.isNotEmpty()) {
            builder.withArrayClaim("permissions", permissions.toTypedArray())
        }

        return builder.sign(algorithm)
    }

    fun decodeToken(token: String): DecodedJWT {
        return JWT.require(algorithm).build().verify(token)
    }
}
