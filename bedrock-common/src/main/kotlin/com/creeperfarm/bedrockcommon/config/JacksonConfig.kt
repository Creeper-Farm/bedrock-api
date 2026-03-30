package com.creeperfarm.bedrockcommon.config

import org.slf4j.LoggerFactory
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer
import tools.jackson.databind.module.SimpleModule
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@Configuration(proxyBeanMethods = false)
class JacksonConfig {

    @Bean
    fun jsonMapperBuilderCustomizer(): JsonMapperBuilderCustomizer {
        return JsonMapperBuilderCustomizer { builder ->
            val timeModule = SimpleModule().apply {
                addSerializer(LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                addSerializer(LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                addSerializer(LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")))
            }

            builder.defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
            builder.defaultDateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            builder.addModule(timeModule)
            builder.configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)

            log.info("Jackson customizer loaded successfully")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JacksonConfig::class.java)
    }
}
