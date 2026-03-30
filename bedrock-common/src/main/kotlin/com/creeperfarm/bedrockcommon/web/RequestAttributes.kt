package com.creeperfarm.bedrockcommon.web

import jakarta.security.auth.message.AuthException
import jakarta.servlet.http.HttpServletRequest

fun HttpServletRequest.requireAuthenticatedUserId(): Long {
    return getAttribute("userId")?.toString()?.toLongOrNull()
        ?: throw AuthException("Missing authenticated user")
}
