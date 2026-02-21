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

    fun createAccessToken(userId: Long, username: String): String =
        generate(userId, username, accessTokenExp)

    fun createRefreshToken(userId: Long, username: String): String =
        generate(userId, username, refreshTokenExp)

    private fun generate(userId: Long, username: String, seconds: Long): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("username", username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + seconds * 1000))
            .sign(algorithm)
    }

    fun decodeToken(token: String): DecodedJWT {
        return JWT.require(algorithm).build().verify(token)
    }
}
