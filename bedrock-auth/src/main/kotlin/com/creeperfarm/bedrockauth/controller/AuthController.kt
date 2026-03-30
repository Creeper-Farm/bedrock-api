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

    /** 登录并返回双 token。 */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, servletRequest: HttpServletRequest): ApiResponse<TokenResponse> {
        log.info("REST request to login user: {}", request.username)
        return ApiResponse.success(authService.login(request, servletRequest))
    }

    /** 使用 refresh token 换发新 token。 */
    @Authenticated
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ApiResponse<TokenResponse> {
        log.info("REST request to refresh token")
        return ApiResponse.success(authService.refreshToken(request.refreshToken))
    }

    /** 清理当前用户的登录态。 */
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
