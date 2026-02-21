package com.creeperfarm.bedrockdevice.service

import com.creeperfarm.bedrockdevice.repository.UserDeviceRepository
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.LocalDateTime

@Service
class UserDeviceService(
    private val userDeviceRepository: UserDeviceRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val HEADER_DEVICE_ID = "X-Device-Id"
        const val HEADER_DEVICE_NAME = "X-Device-Name"
        const val HEADER_OS = "X-OS"
        const val HEADER_APP_VERSION = "X-App-Version"
    }

    @Transactional
    fun recordLoginDevice(userId: Long, request: HttpServletRequest, ipAddress: String?) {
        // 标准化请求端特征并生成设备标识
        val userAgent = request.getHeader("User-Agent")
        val normalizedUa = userAgent?.ifBlank { null } ?: "unknown"
        val os = request.getHeader(HEADER_OS)?.takeIf { it.isNotBlank() } ?: parseOs(normalizedUa)
        val clientName = parseClientName(normalizedUa)
        val appVersion = request.getHeader(HEADER_APP_VERSION)?.takeIf { it.isNotBlank() }
        val deviceName = request.getHeader(HEADER_DEVICE_NAME)?.takeIf { it.isNotBlank() }
            ?: "$clientName/$os"
        val deviceId = resolveDeviceId(
            providedDeviceId = request.getHeader(HEADER_DEVICE_ID),
            userAgent = normalizedUa,
            os = os
        )
        val now = LocalDateTime.now()

        val existing = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
        if (existing == null) {
            // 首次设备登录：创建新记录
            userDeviceRepository.createLoginRecord(
                userId = userId,
                deviceId = deviceId,
                deviceName = deviceName,
                appVersion = appVersion,
                os = os,
                ipAddress = ipAddress,
                userAgent = normalizedUa,
                now = now
            )
            log.info("Recorded new login device for userId: {}", userId)
            return
        }

        // 已知设备再次登录：递增登录次数并刷新最后登录信息
        userDeviceRepository.updateLoginRecord(
            userId = userId,
            deviceId = deviceId,
            loginCount = existing.loginCount + 1,
            appVersion = appVersion,
            ipAddress = ipAddress,
            now = now
        )
        log.info("Updated login device for userId: {}, deviceId: {}", userId, deviceId)
    }

    private fun parseOs(userAgent: String): String {
        val ua = userAgent.lowercase()
        return when {
            "harmony" in ua || "openharmony" in ua || "hmos" in ua -> "HarmonyOS"
            "windows" in ua -> "Windows"
            "mac os" in ua || "macintosh" in ua -> "macOS"
            "android" in ua -> "Android"
            "iphone" in ua || "ipad" in ua || "ios" in ua -> "iOS"
            "linux" in ua -> "Linux"
            else -> "Unknown OS"
        }
    }

    private fun parseClientName(userAgent: String): String {
        val ua = userAgent.lowercase()
        return when {
            "edg/" in ua -> "Edge"
            "chrome/" in ua && "edg/" !in ua -> "Chrome"
            "firefox/" in ua -> "Firefox"
            "safari/" in ua && "chrome/" !in ua -> "Safari"
            "micromessenger/" in ua -> "WeChat"
            "okhttp/" in ua || "dalvik/" in ua || "cfnetwork/" in ua -> "App"
            else -> "Unknown"
        }
    }

    private fun resolveDeviceId(
        providedDeviceId: String?,
        userAgent: String,
        os: String
    ): String {
        val customId = providedDeviceId?.trim()?.takeIf { it.isNotBlank() }
        if (customId != null) {
            // 客户端可显式上报稳定 deviceId，后端统一做摘要存储
            return buildDeviceId(customId, os, "custom")
        }
        // 浏览器或未上报 deviceId 的场景，回退到 UA 特征指纹
        return buildDeviceId(userAgent, os, "ua")
    }

    private fun buildDeviceId(identifierSource: String, os: String, fingerprintType: String): String {
        // 设备指纹：不暴露原始标识，使用 SHA-256 存储固定长度摘要
        val input = "$identifierSource|$os|$fingerprintType"
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
