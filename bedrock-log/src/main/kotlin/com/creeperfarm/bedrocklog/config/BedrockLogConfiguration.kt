package com.creeperfarm.bedrocklog.config

import com.creeperfarm.bedrocklog.service.OperationLogService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BedrockLogConfiguration {

    @Bean
    fun operationLogService(): OperationLogService {
        return OperationLogService()
    }
}
