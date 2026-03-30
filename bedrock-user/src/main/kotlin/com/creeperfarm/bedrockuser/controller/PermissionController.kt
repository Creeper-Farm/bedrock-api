package com.creeperfarm.bedrockuser.controller

import com.creeperfarm.bedrockcommon.annotation.Authenticated
import com.creeperfarm.bedrockcommon.annotation.RequiresPermissions
import com.creeperfarm.bedrockcommon.model.response.ApiResponse
import com.creeperfarm.bedrockcommon.model.response.PageResponse
import com.creeperfarm.bedrockuser.model.request.PermissionCreateRequest
import com.creeperfarm.bedrockuser.model.request.PermissionUpdateRequest
import com.creeperfarm.bedrockuser.model.response.PermissionResponse
import com.creeperfarm.bedrockuser.service.PermissionService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    /** 分页查询权限列表。 */
    @Authenticated
    @RequiresPermissions(["system:permission:list"])
    @GetMapping("/list")
    fun listPermissions(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?,
    ): ApiResponse<PageResponse<PermissionResponse>> {
        logger.info("REST request to get permissions page: $page, pageSize: $size, name: $name, Search: $name")
        val permissions = permissionService.listPermissions(page, size, name)
        val total = permissionService.countPermissions(name)
        return ApiResponse.success(PageResponse.of(total, permissions, page, size))
    }

    /** 创建权限。 */
    @Authenticated
    @RequiresPermissions(["system:permission:create"])
    @PostMapping("/create")
    fun createPermission(
        @RequestBody @Valid request: PermissionCreateRequest
    ): ApiResponse<Long> {
        val permissionId = permissionService.createPermission(request.name, request.code, request.type)
        return ApiResponse.success(permissionId)
    }

    /** 更新权限。 */
    @Authenticated
    @RequiresPermissions(["system:permission:update"])
    @PutMapping("/update")
    fun updatePermission(
        @RequestBody @Valid request: PermissionUpdateRequest
    ): ApiResponse<Boolean> {
        val isUpdate = permissionService.updatePermission(request.id, request.name, request.code, request.type)
        return ApiResponse.success(isUpdate)
    }

    /** 物理删除权限及其角色关联。 */
    @Authenticated
    @RequiresPermissions(["system:permission:delete"])
    @DeleteMapping("/delete/{permissionId:\\d+}")
    fun deletePermission(
        @PathVariable permissionId: Long
    ): ApiResponse<Boolean> {
        logger.info("REST request to delete permission: {}", permissionId)
        val isDeleted = permissionService.deletePermission(permissionId)
        return ApiResponse.success(isDeleted)
    }

}
