package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.response.ApiResponse
import com.creeperfarm.bedrockuser.model.request.RolePermissionUpdateRequest
import com.creeperfarm.bedrockuser.model.request.UserRoleUpdateRequest
import com.creeperfarm.bedrockuser.model.response.RoleResponse
import com.creeperfarm.bedrockuser.model.response.UserResponse
import com.creeperfarm.bedrockuser.service.RoleService
import com.creeperfarm.bedrockuser.service.UserService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/role")
class RoleController(
    private val roleService: RoleService,
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(RoleController::class.java)

    /**
     * 获取角色列表
     */
    @Authenticated
    @RequiresPermissions(["system:role:list"])
    @GetMapping("/list")
    fun listRoles(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?
    ): ApiResponse<List<RoleResponse>> {
        logger.info("REST request to get all roles. Page: $page, Size: $size, Name: $name")
        val roles = roleService.listRoles(page, size, name)
        return ApiResponse.success(roles)
    }

    /**
     * 根据用户 ID 获取角色列表
     */
    @Authenticated
    @RequiresPermissions(["system:role:list"])
    @GetMapping("/user/{userId:\\d+}")
    fun listRolesByUserId(@PathVariable userId: Long): ApiResponse<List<RoleResponse>> {
        logger.info("REST request to get roles by userId: {}", userId)
        val roles = roleService.listRolesByUserId(userId)
        return ApiResponse.success(roles)
    }

    /**
     * 根据角色 ID 获取用户列表
     */
    @Authenticated
    @RequiresPermissions(["system:user:list"])
    @GetMapping("/{roleId:\\d+}/users")
    fun listUsersByRoleId(
        @PathVariable roleId: Long,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ApiResponse<List<UserResponse>> {
        logger.info("REST request to get users by roleId: {}. Page: {}, Size: {}", roleId, page, size)
        val users = userService.listUsersByRoleId(page, size, roleId)
        return ApiResponse.success(users)
    }

    /**
     * 为用户重新绑定角色列表
     */
    @Authenticated
    @RequiresPermissions(["system:user:role"])
    @PutMapping("/user/{userId:\\d+}")
    fun updateUserRoles(
        @PathVariable userId: Long,
        @RequestBody @Valid request: UserRoleUpdateRequest
    ): ApiResponse<Boolean> {
        logger.info("REST request to update roles for userId: {}", userId)
        val isUpdate = roleService.updateUserRoleAssignments(userId, request.roleIds)
        return ApiResponse.success(isUpdate)
    }

    /**
     * 为角色绑定权限列表
     */
    @Authenticated
    @RequiresPermissions(["system:role:permission"])
    @PutMapping("{roleId:\\d+}/permissions")
    fun updateRolePermissions(
        @PathVariable roleId: Long,
        @RequestBody @Valid request: RolePermissionUpdateRequest
    ): ApiResponse<Boolean> {
        logger.info("REST request to update permissions for roleId: {}", roleId)
        val isUpdated = roleService.updateRolePermissionAssignments(roleId, request.permissionIds)
        return ApiResponse.success(isUpdated)
    }

}
