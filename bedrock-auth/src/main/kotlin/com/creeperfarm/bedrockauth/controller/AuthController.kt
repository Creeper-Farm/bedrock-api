package com.creeperfarm.bedrockauth.controller

import com.creeperfarm.bedrockauth.model.request.LoginRequest
import com.creeperfarm.bedrockauth.model.request.RefreshTokenRequest
import com.creeperfarm.bedrockauth.model.response.TokenResponse
import com.creeperfarm.bedrockauth.service.AuthService
import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockcommon.model.response.ApiResponse
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 登录接口
     * 注释：验证通过后返回 AccessToken 和 RefreshToken
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, servletRequest: HttpServletRequest): ApiResponse<TokenResponse> {
        log.info("REST request to login user: {}", request.username)
        return ApiResponse.success(authService.login(request, servletRequest))
    }

    /**
     * 刷新 Token 接口
     * 注释：前端在 AccessToken 过期后，携带 refreshToken 请求此接口
     */
    @Authenticated
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ApiResponse<TokenResponse> {
        log.info("REST request to refresh token")
        return ApiResponse.success(authService.refreshToken(request.refreshToken))
    }

    /**
     * 注销接口
     * 注释：删除当前用户在 Redis 中的 AccessToken 与 RefreshToken
     */
    @Authenticated
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ApiResponse<Unit> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to logout userId: {}", userId)
        authService.logout(userId)
        return ApiResponse.success(null)
    }
}
