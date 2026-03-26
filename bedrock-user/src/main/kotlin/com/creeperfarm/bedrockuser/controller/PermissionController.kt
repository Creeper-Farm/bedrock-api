package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.dto.PageResult
import com.creeperfarm.bedrockcommon.model.dto.Result
import com.creeperfarm.bedrockuser.model.dto.PermissionCreate
import com.creeperfarm.bedrockuser.model.dto.PermissionResponse
import com.creeperfarm.bedrockuser.service.PermissionService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/permissions")
class PermissionController(
    private val permissionService: PermissionService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 分页查询权限列表
     * 注释：需要管理员权限 system:permission:list
     */
    @Authenticated
    @RequiresPermissions(["system:permission:list"])
    @GetMapping("/list")
    fun listPermissions(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?,
    ): Result<PageResult<PermissionResponse>> {
        logger.info("REST request to get permissions page: $page, pageSize: $size, name: $name, Search: $name")
        val permissions = permissionService.getPermissions(page, size, name)
        val total = permissionService.getSearchPermissionTotal(name)
        return Result.success(PageResult.of(total, permissions, page, size))
    }

    /**
     * 创建权限
     * 注释：需要管理员权限 system:permission:create
     */
    @Authenticated
    @RequiresPermissions(["system:permission:create"])
    @PostMapping("/create")
    fun createPermission(
        @RequestBody @Valid request: PermissionCreate
    ): Result<Long> {
        val permissionId = permissionService.createPermission(request.name, request.code)
        return Result.success(permissionId)
    }

}
