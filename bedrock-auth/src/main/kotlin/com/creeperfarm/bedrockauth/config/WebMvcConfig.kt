package com.creeperfarm.bedrockauth.config

import com.creeperfarm.bedrockauth.interceptor.JwtInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(private val jwtInterceptor: JwtInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        // 拦截所有 API 路径，内部逻辑会根据注解判断是否真的需要 Token
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/**") // 拦截所有以 /api 开头的请求
    }
}
