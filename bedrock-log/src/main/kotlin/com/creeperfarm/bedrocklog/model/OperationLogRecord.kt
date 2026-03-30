package com.creeperfarm.bedrocklog.model

import java.time.LocalDateTime

data class OperationLogRecord(
    val module: String,
    val action: String,
    val operator: String? = null,
    val detail: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
