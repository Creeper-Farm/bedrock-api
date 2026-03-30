package com.creeperfarm.bedrocklog

import com.creeperfarm.bedrocklog.config.BedrockLogConfiguration
import com.creeperfarm.bedrocklog.service.OperationLogService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest(classes = [BedrockLogConfiguration::class])
class BedrockLogApplicationTests {

    @Autowired
    private lateinit var operationLogService: OperationLogService

    @Test
    fun contextLoads() {
        check(::operationLogService.isInitialized)
    }

}
