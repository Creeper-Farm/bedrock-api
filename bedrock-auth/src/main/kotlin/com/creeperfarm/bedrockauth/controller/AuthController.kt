package com.creeperfarm.bedrockauth.controller

import com.creeperfarm.bedrockauth.model.dto.TokenResponse
import com.creeperfarm.bedrockauth.service.AuthService
import com.creeperfarm.bedrockuser.model.dto.UserRegister
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import com.creeperfarm.bedrockcommon.model.Result

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 登录接口
     * 注释：验证通过后返回 AccessToken 和 RefreshToken
     */
    @PostMapping("/login")
    fun login(@RequestBody req: UserRegister): Result<TokenResponse> {
        log.info("REST request to login user: {}", req.username)
        return Result.success(authService.login(req))
    }

    /**
     * 刷新 Token 接口
     * 注释：前端在 AccessToken 过期后，携带 refreshToken 请求此接口
     */
    @PostMapping("/refresh")
    fun refresh(@RequestParam refreshToken: String): Result<TokenResponse> {
        log.info("REST request to refresh token")
        return Result.success(authService.refreshToken(refreshToken))
    }

    /**
     * 注销接口
     * 注释：删除当前用户在 Redis 中的 AccessToken 与 RefreshToken
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): Result<Unit> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to logout userId: {}", userId)
        authService.logout(userId)
        return Result.success(null)
    }
}
