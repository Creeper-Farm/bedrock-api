package com.creeperfarm.bedrockauth.config

import com.creeperfarm.bedrockauth.interceptor.JwtInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(private val jwtInterceptor: JwtInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/**") // 拦截所有以 /api 开头的请求
            .excludePathPatterns(
                // --- 白名单：不需要 Token 的接口 ---
                "/api/auth/login",
                "/api/auth/refresh",
                "/api/users/register",

                // --- 静态资源与监控 ---
                "/actuator/**",
                "/error"
            )
    }
}