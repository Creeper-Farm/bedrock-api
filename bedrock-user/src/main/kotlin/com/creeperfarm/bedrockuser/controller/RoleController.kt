package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.dto.Result
import com.creeperfarm.bedrockuser.model.dto.RoleResponse
import com.creeperfarm.bedrockuser.model.dto.UserResponse
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
    fun getRoleList(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?
    ): Result<List<RoleResponse>> {
        logger.info("REST request to get all roles. Page: $page, Size: $size, Name: $name")
        val roles = roleService.getRoleList(page, size, name)
        return Result.success(roles)
    }

    /**
     * 根据用户 ID 获取角色列表
     */
    @Authenticated
    @RequiresPermissions(["system:role:list"])
    @GetMapping("/user/{userId:\\d+}")
    fun getRoleListByUserId(@PathVariable userId: Long): Result<List<RoleResponse>> {
        logger.info("REST request to get roles by userId: {}", userId)
        val roles = roleService.getRoleListByUserId(userId)
        return Result.success(roles)
    }

    /**
     * 根据角色 ID 获取用户列表
     */
    @Authenticated
    @RequiresPermissions(["system:user:list"])
    @GetMapping("/{roleId:\\d+}/users")
    fun getUserListByRoleId(
        @PathVariable roleId: Long,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Result<List<UserResponse>> {
        logger.info("REST request to get users by roleId: {}. Page: {}, Size: {}", roleId, page, size)
        val users = userService.getUserListByRoleId(page, size, roleId)
        return Result.success(users)
    }

    /**
     * 为用户重新绑定角色列表
     */
    @Authenticated
    @RequiresPermissions(["system:user:role"])
    @PutMapping("/user/{userId:\\d+}")
    fun updateUserRoles(
        @PathVariable userId: Long,
        @RequestBody @Valid roleIds: List<Long>
    ): Result<Boolean> {
        logger.info("REST request to update roles for userId: {}", userId)
        val isUpdate = roleService.updateUserRoles(userId, roleIds)
        return Result.success(isUpdate)
    }

    /**
     * 为角色绑定权限列表
     */
    @Authenticated
    @RequiresPermissions(["system:role:permission"])
    @PutMapping("{roleId:\\d+}/permissions")
    fun assignPermissions(
        @PathVariable roleId: Long,
        @RequestBody permissionIds: List<Long>
    ): Result<Boolean> {
        logger.info("REST request to update permissions for roleId: {}", roleId)
        val isUpdated = roleService.updateRolePermissions(roleId, permissionIds)
        return Result.success(isUpdated)
    }

}
