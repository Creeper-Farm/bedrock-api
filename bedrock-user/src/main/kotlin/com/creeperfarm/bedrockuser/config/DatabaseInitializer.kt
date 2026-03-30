package com.creeperfarm.bedrockuser.config

import com.creeperfarm.bedrockuser.model.entity.PermissionTable
import com.creeperfarm.bedrockuser.model.entity.RolePermissionTable
import com.creeperfarm.bedrockuser.model.entity.UserTable
import com.creeperfarm.bedrockuser.model.entity.RoleTable
import com.creeperfarm.bedrockuser.model.entity.UserDeviceTable
import com.creeperfarm.bedrockuser.model.entity.UserRoleTable
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component("userDatabaseInitializer")
@Order(10)
class DatabaseInitializer : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(vararg args: String) {
        log.info("Starting database schema synchronization check...")

        // MigrationUtils 会自动计算这些表与数据库当前状态的差异
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

        // 执行迁移 SQL
        statements.forEach { sql ->
            log.info("Executing migration SQL: {}", sql)
            try {
                currentTransaction.exec(sql)
            } catch (e: Exception) {
                log.error("Failed to execute migration SQL: {}. Error: {}", sql, e.message)
                // 抛出异常以触发 @Transactional 回滚
                throw e
            }
        }

        log.info("Database schema synchronization completed. {} statements executed.", statements.size)
    }
}
