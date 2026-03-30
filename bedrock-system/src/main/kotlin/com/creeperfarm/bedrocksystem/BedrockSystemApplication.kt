package com.creeperfarm.bedrocksystem

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.creeperfarm"])
@EnableAdminServer
class BedrockSystemApplication

fun main(args: Array<String>) {
    runApplication<BedrockSystemApplication>(*args)
}
