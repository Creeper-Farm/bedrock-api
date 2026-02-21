package com.creeperfarm.bedrockauth.model.dto

/**
 * Token Response DTO
 * 注释：返回给前端的双 Token 结构
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long, // 单位：秒
    val refreshTokenExpiresIn: Long
)