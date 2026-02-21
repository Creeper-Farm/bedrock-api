package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockuser.model.dto.UserRegister
import com.creeperfarm.bedrockuser.model.dto.UserProfileUpdate
import com.creeperfarm.bedrockuser.model.dto.UserResponse
import com.creeperfarm.bedrockuser.service.UserService
import com.creeperfarm.bedrockcommon.model.Result
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 用户注册接口
     * 注释：接收 UserRegister DTO，返回新创建的用户 ID
     */
    @PostMapping("/register")
    fun register(@RequestBody @Valid req: UserRegister): Result<Long> {
        log.info("REST request to register user: {}", req.username)
        val userId = userService.registerUser(req)
        return Result.success(userId)
    }

    /**
     * 获取用户信息接口
     * 注释：根据用户名获取活跃（未删除）的用户资料
     */
    @GetMapping("/{userId:\\d+}")
    fun getProfile(@PathVariable userId: Long): Result<UserResponse> {
        log.info("REST request to get user profile: {}", userId)
        val profile = userService.getUserProfile(userId)
        return Result.success(profile)
    }

    /**
     * 注销账号（软删除）接口
     */
    @DeleteMapping("/account")
    fun deleteAccount(request: HttpServletRequest): Result<Unit> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to soft delete account, userId: {}", userId)
        userService.deleteAccount(userId)
        return Result.success(null)
    }

    /**
     * 更新当前用户资料接口
     */
    @PutMapping("/profile")
    fun updateProfile(
        request: HttpServletRequest,
        @RequestBody @Valid req: UserProfileUpdate
    ): Result<UserResponse> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to update profile, userId: {}", userId)
        return Result.success(userService.updateUserProfile(userId, req))
    }
}
