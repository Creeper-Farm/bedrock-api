package com.creeperfarm.bedrockauth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration("authSecurityConfig")
class AuthSecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // 禁用 CSRF，因为我们用 JWT
            .authorizeHttpRequests {
                // 全部放行，具体的权限控制交给我们的 JwtInterceptor 逻辑处理
                it.anyRequest().permitAll()
            }
            .formLogin { it.disable() } // 禁用默认登录页
            .httpBasic { it.disable() } // 禁用 Basic 认证

        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}