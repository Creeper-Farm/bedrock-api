package com.creeperfarm.bedrockcommon.annotation

import com.creeperfarm.bedrockcommon.model.enums.PermissionMatchMode

/**
 * 权限校验注解
 * 用于标注接口所需的权限字符，例如 "system:user:add"
 * * @param value 权限字符数组
 * @param matchMode 匹配关系：AND (全部符合) 或 OR (符合其一)，默认 AND
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(
    val value: Array<String>,
    val matchMode: PermissionMatchMode = PermissionMatchMode.AND
)
