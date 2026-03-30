package com.creeperfarm.bedrocklog.service

import com.creeperfarm.bedrocklog.model.OperationLogRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OperationLogService {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun write(record: OperationLogRecord): OperationLogRecord {
        logger.info(
            "operation log recorded. module: {}, action: {}, operator: {}, detail: {}",
            record.module,
            record.action,
            record.operator,
            record.detail
        )
        return record
    }
}
