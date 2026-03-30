package com.creeperfarm.bedrocksystem.config

import com.creeperfarm.bedrockuser.model.entity.PermissionTable
import com.creeperfarm.bedrockuser.model.entity.RolePermissionTable
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserDeviceTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import com.creeperfarm.bedrockuser.model.entity.UserTable
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component("databaseSchemaMigrationRunner")
@Order(10)
class DatabaseSchemaMigrationConfiguration : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(vararg args: String) {
        log.info("Starting database schema synchronization check...")

        val statements = MigrationUtils.statementsRequiredForDatabaseMigration(
            UserTable,
            RoleTable,
            UserRoleTable,
            PermissionTable,
            RolePermissionTable,
            UserDeviceTable
        )

        if (statements.isEmpty()) {
            log.info("Database schema is up to date. No migration required.")
            return
        }

        val currentTransaction = TransactionManager.current()
        statements.forEach { sql ->
            log.info("Executing migration SQL: {}", sql)
            try {
                currentTransaction.exec(sql)
            } catch (e: Exception) {
                log.error("Failed to execute migration SQL: {}. Error: {}", sql, e.message)
                throw e
            }
        }

        log.info("Database schema synchronization completed. {} statements executed.", statements.size)
    }
}
