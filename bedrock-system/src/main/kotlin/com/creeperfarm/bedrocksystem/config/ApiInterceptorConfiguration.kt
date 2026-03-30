package com.creeperfarm.bedrocksystem.config

import com.creeperfarm.bedrockauth.interceptor.JwtInterceptor
import com.creeperfarm.bedrockuser.interceptor.PermissionInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
class ApiInterceptorConfiguration(
    private val jwtInterceptor: JwtInterceptor,
    private val permissionInterceptor: PermissionInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/api/**")
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/api/**")
    }
}
