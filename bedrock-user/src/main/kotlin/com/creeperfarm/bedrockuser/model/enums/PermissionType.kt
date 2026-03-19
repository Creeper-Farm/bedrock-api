package com.creeperfarm.bedrockuser.model.enums

enum class PermissionType(val value: Int, val description: String) {
    MENU(0, "菜单"),
    BUTTON(1, "按钮"),
    INTERFACE(2, "接口")
}