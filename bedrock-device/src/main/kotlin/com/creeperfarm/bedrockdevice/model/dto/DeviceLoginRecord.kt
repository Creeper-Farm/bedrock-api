package com.creeperfarm.bedrockdevice.model.dto

data class DeviceLoginRecord(
    val userId: Long,
    val deviceId: String,
    val loginCount: Int
)
