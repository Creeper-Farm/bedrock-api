package com.creeperfarm.bedrockdevice.config

import com.creeperfarm.bedrockdevice.model.entity.UserDeviceTable
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.transaction.annotation.Transactional

@Configuration("deviceDatabaseInitializer")
@Order(20)
class DatabaseInitializer : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(vararg args: String?) {
        log.info("Starting device schema synchronization check...")

        val statements = MigrationUtils.statementsRequiredForDatabaseMigration(UserDeviceTable)
        if (statements.isEmpty()) {
            log.info("Device schema is up to date. No migration required.")
            return
        }

        val currentTransaction = TransactionManager.current()
        statements.forEach { sql ->
            log.info("Executing device migration SQL: {}", sql)
            try {
                currentTransaction.exec(sql)
            } catch (e: Exception) {
                log.error("Failed to execute device migration SQL: {}. Error: {}", sql, e.message)
                throw e
            }
        }

        log.info("Device schema synchronization completed. {} statements executed.", statements.size)
    }
}
