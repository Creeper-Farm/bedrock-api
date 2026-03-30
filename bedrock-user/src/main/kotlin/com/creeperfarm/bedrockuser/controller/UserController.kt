package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.response.ApiResponse
import com.creeperfarm.bedrockuser.model.request.UserProfileUpdateRequest
import com.creeperfarm.bedrockuser.model.request.UserRegistrationRequest
import com.creeperfarm.bedrockuser.model.response.UserResponse
import com.creeperfarm.bedrockuser.service.UserService
import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    private val log = LoggerFactory.getLogger(javaClass)

    /** 注册用户。 */
    @PostMapping("/register")
    fun register(@RequestBody @Valid request: UserRegistrationRequest): ApiResponse<Long> {
        log.info("REST request to register user: {}", request.username)
        val userId = userService.register(request)
        return ApiResponse.success(userId)
    }

    /** 查询指定用户。 */
    @Authenticated
    @GetMapping("/{userId:\\d+}")
    fun getUserById(@PathVariable userId: Long): ApiResponse<UserResponse> {
        log.info("REST request to get user profile: {}", userId)
        val profile = userService.getUserById(userId)
        return ApiResponse.success(profile)
    }

    /** 查询当前登录用户。 */
    @Authenticated
    @GetMapping("/profile")
    fun getCurrentUserProfile(request: HttpServletRequest): ApiResponse<UserResponse> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to get user profile: $userId")
        val profile = userService.getUserById(userId)
        return ApiResponse.success(profile)
    }

    /** 软删除当前账号。 */
    @Authenticated
    @DeleteMapping("/account")
    fun deleteAccount(request: HttpServletRequest): ApiResponse<Unit> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to soft delete account, userId: {}", userId)
        userService.deleteAccount(userId)
        return ApiResponse.success(null)
    }

    /** 更新当前用户资料。 */
    @Authenticated
    @PutMapping("/profile")
    fun updateProfile(
        request: HttpServletRequest,
        @RequestBody @Valid updateRequest: UserProfileUpdateRequest
    ): ApiResponse<UserResponse> {
        val userId = request.getAttribute("userId")?.toString()?.toLongOrNull()
            ?: throw AuthException("Missing authenticated user")
        log.info("REST request to update profile, userId: {}", userId)
        return ApiResponse.success(userService.updateProfile(userId, updateRequest))
    }

    /** 分页查询用户列表。 */
    @Authenticated
    @RequiresPermissions(["system:user:list"])
    @GetMapping("/list")
    fun listUsers(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) username: String?
    ): ApiResponse<List<UserResponse>> {
        log.info("REST request to get user list. Page: {}, Size: {}, Search: {}", page, size, username)
        val users = userService.listUsers(page, size, username)
        return ApiResponse.success(users)
    }
}
