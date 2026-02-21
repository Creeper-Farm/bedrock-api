package com.creeperfarm.bedrockcommon.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.format.DateTimeFormatter

@Configuration
class JacksonConfig {

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            // 设置系统标准时区为东八区
            builder.timeZone("GMT+8")

            // 设置 Date 类型的格式化模式
            builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss")

            // 针对 Java 8 的日期时间类型 (JSR310) 设置序列化器
            builder.serializers(LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            builder.serializers(LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            builder.serializers(LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")))

            // 禁用“将日期写为时间戳”的特性，防止 LocalDateTime 变成数字数组
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

            log.info("Jackson customizer loaded successfully")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JacksonConfig::class.java)
    }
}