package com.creeperfarm.bedrockauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "auth.jwt")
class AuthJwtProperties {
    lateinit var secret: String
    var accessTokenExpSeconds: Long = 3600
    var refreshTokenExpSeconds: Long = 86400L * 7
}
