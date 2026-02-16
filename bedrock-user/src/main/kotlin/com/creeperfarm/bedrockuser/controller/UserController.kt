package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockuser.model.dto.UserRegister
import com.creeperfarm.bedrockuser.model.dto.UserResponse
import com.creeperfarm.bedrockuser.service.UserService
import com.creeperfarm.bedrockcommon.model.Result
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
    @GetMapping("/{username}")
    fun getProfile(@PathVariable username: String): Result<UserResponse> {
        log.info("REST request to get user profile: {}", username)
        val profile = userService.getUserProfile(username)
        return Result.success(profile)
    }
}