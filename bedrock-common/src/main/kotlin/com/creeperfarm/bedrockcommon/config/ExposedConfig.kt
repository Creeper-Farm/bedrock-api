package com.creeperfarm.bedrockcommon.config

import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.spring.transaction.ExposedSpringTransactionAttributeSource
import org.jetbrains.exposed.v1.spring.transaction.SpringTransactionManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class ExposedConfig(
    @Value("\${spring.exposed.show-sql:false}")
    private val showSql: Boolean
) {

    @Bean
    fun databaseConfig(): DatabaseConfig {
        return DatabaseConfig {}
    }

    @Bean
    fun springTransactionManager(
        dataSource: DataSource,
        databaseConfig: DatabaseConfig
    ): SpringTransactionManager {
        return SpringTransactionManager(dataSource, databaseConfig, showSql)
    }

    @Bean
    @Primary
    fun exposedSpringTransactionAttributeSource(): ExposedSpringTransactionAttributeSource {
        return ExposedSpringTransactionAttributeSource()
    }
}
