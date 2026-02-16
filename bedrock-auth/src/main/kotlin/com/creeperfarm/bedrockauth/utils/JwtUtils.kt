package com.creeperfarm.bedrockauth.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils {
    // 注释：生产环境建议从配置文件读取
    private val secret = "bedrock-secret-key-2026"
    private val algorithm = Algorithm.HMAC256(secret)

    // 有效期定义
    val accessTokenExp = 3600L       // 1小时
    val refreshTokenExp = 86400L * 7 // 7天

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