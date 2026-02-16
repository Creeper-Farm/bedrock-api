package com.creeperfarm.bedrockuser.config

import com.creeperfarm.bedrockuser.model.entity.UserTable
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional

@Configuration
class DatabaseInitializer : CommandLineRunner {

    // 定义 Logger
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(vararg args: String?) {
        // 开始检查数据库结构
        log.info("Starting database schema synchronization check...")

        // 1. 获取全量同步所需的 SQL 语句
        val statements = MigrationUtils.statementsRequiredForDatabaseMigration(UserTable)

        if (statements.isEmpty()) {
            // 数据库已经是最新状态
            log.info("Database schema is up to date. No migration required.")
            return
        }

        val currentTransaction = TransactionManager.current()

        // 2. 执行迁移
        statements.forEach { sql ->
            // 记录正在执行的 SQL 指令
            log.info("Executing migration SQL: {}", sql)

            try {
                currentTransaction.exec(sql)
            } catch (e: Exception) {
                // 记录执行失败的错误日志
                log.error("Failed to execute migration SQL: {}. Error: {}", sql, e.message)
                throw e // 抛出异常触发事务回滚
            }
        }

        // 结构同步完成
        log.info("Database schema synchronization completed. {} statements executed.", statements.size)
    }
}