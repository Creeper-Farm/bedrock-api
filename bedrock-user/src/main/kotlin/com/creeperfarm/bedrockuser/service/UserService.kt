package com.creeperfarm.bedrockuser.service

import com.creeperfarm.bedrockuser.model.request.UserProfileUpdateRequest
import com.creeperfarm.bedrockuser.model.request.UserRegistrationRequest
import com.creeperfarm.bedrockuser.model.response.UserResponse
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
    private val log = LoggerFactory.getLogger(javaClass)

    /** 注册新用户。 */
    @Transactional
    fun register(request: UserRegistrationRequest): Long {
        log.info("Attempting to register new user: {}", request.username)

        val existingUser = userRepository.findByUsername(request.username)
        if (existingUser != null) {
            log.warn("Registration failed: Username '{}' is already taken", request.username)
            throw RuntimeException("Username already exists")
        }

        val encodedPassword = requireNotNull(passwordEncoder.encode(request.password)) {
            "Encoded password must not be null"
        }

        val userId = userRepository.createUser(request, encodedPassword)

        log.info("User '{}' registered successfully with ID: {}", request.username, userId)
        return userId
    }

    /** 查询单个用户。 */
    @Transactional(readOnly = true)
    fun getUserById(userId: Long): UserResponse {
        log.info("Fetching profile for userId: {}", userId)
        return userRepository.findByUserId(userId) ?: run {
            log.warn("Profile fetch failed: User '{}' not found or deleted", userId)
            throw RuntimeException("User not found")
        }
    }

    /** 软删除当前账号。 */
    @Transactional
    fun deleteAccount(userId: Long) {
        val affectedRows = userRepository.softDeleteUser(userId)
        if (affectedRows == 0) {
            log.warn("Delete account failed: User '{}' not found or already deleted", userId)
            throw RuntimeException("User not found or already deleted")
        }
        log.info("User account soft deleted successfully, userId: {}", userId)
    }

    /** 更新当前用户资料。 */
    @Transactional
    fun updateProfile(userId: Long, request: UserProfileUpdateRequest): UserResponse {
        if (request.email == null && request.phone == null && request.avatar == null && request.bio == null) {
            throw IllegalArgumentException("At least one field must be provided")
        }

        val affectedRows = userRepository.updateUserProfile(userId, request)
        if (affectedRows == 0) {
            log.warn("Update profile failed: User '{}' not found or deleted", userId)
            throw RuntimeException("User not found or already deleted")
        }

        return userRepository.findByUserId(userId) ?: throw RuntimeException("User not found")
    }

    /** 分页查询用户列表。 */
    @Transactional(readOnly = true)
    fun listUsers(page: Int, size: Int, username: String?): List<UserResponse> {
        log.info("Fetching user list with pagination - Page: {}, Size: {}", page, size)

        val offset = ((page - 1) * size).toLong()
        return userRepository.findUsers(offset, size, username)
    }

    /** 分页查询角色下的用户列表。 */
    @Transactional(readOnly = true)
    fun listUsersByRoleId(page: Int, size: Int, roleId: Long): List<UserResponse> {
        log.info("Fetching user list by roleId with pagination - Page: {}, Size: {}", page, size)
        val offset = ((page - 1) * size).toLong()
        return userRepository.findUsersByRoleId(offset, size, roleId)
    }
}
