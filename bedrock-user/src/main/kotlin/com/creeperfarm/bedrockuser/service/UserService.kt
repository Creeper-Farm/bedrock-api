package com.creeperfarm.bedrockuser.service

import com.creeperfarm.bedrockuser.model.dto.UserRegister
import com.creeperfarm.bedrockuser.model.dto.UserResponse
import com.creeperfarm.bedrockuser.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    // 定义日志对象
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 注册新用户
     * 使用写事务，发生异常时会自动回滚
     */
    @Transactional
    fun registerUser(req: UserRegister): Long {
        log.info("Attempting to register new user: {}", req.username)

        val existingUser = userRepository.findByUsername(req.username)
        if (existingUser != null) {
            log.warn("Registration failed: Username '{}' is already taken", req.username)
            throw RuntimeException("Username already exists")
        }

        val encodedPassword = passwordEncoder.encode(req.password)

        val userId = userRepository.createUser(req, encodedPassword)

        log.info("User '{}' registered successfully with ID: {}", req.username, userId)
        return userId
    }

    /**
     * 获取用户信息
     * 使用只读事务 (readOnly = true)，优化数据库性能
     */
    @Transactional(readOnly = true)
    fun getUserProfile(username: String): UserResponse {
        log.info("Fetching profile for username: {}", username)
        return userRepository.findByUsername(username) ?: run {
            log.warn("Profile fetch failed: User '{}' not found or deleted", username)
            throw RuntimeException("User not found")
        }
    }
}