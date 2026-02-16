package com.creeperfarm.bedrockuser.model.enums

/**
 * 用户状态枚举
 */
enum class UserStatus(val value: Int, val description: String) {
    NORMAL(0, "正常"),
    DISABLED(1, "禁用"),
    LOCKED(2, "锁定"),
    CANCELED(3, "注销")
}