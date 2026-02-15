package com.creeperfarm.bedrocksystem.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    /**
     * 解决跨域问题
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 所有接口
            .allowedOriginPatterns("*") // 允许任何来源
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }

}