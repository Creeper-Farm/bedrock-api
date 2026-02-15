package com.creeperfarm.bedrocksystem

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.creeperfarm"])
@EnableJpaRepositories(basePackages = ["com.creeperfarm"])
@EntityScan(basePackages = ["com.creeperfarm"])
@EnableAdminServer
class BedrockSystemApplication

fun main(args: Array<String>) {
    runApplication<BedrockSystemApplication>(*args)
}