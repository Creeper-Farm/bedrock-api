package com.creeperfarm.bedrockuser.model.response

data class UserDeviceLoginRecord(
    val userId: Long,
    val deviceId: String,
    val loginCount: Int
)
