package com.creeperfarm.bedrocklog

import com.creeperfarm.bedrocklog.service.OperationLogService
import org.springframework.beans.factory.annotation.Autowired
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [OperationLogService::class])
class BedrockLogApplicationTests {

    @Autowired
    private lateinit var operationLogService: OperationLogService

    @Test
    fun contextLoads() {
        check(::operationLogService.isInitialized)
    }

}
