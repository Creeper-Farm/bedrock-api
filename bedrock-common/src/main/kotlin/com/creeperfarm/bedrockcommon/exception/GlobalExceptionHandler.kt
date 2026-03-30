package com.creeperfarm.bedrockcommon.exception

import com.creeperfarm.bedrockcommon.model.response.ApiResponse
import jakarta.security.auth.message.AuthException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.nio.file.AccessDeniedException
import javax.security.sasl.AuthenticationException

/** 统一映射接口异常响应。 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /** 兜底处理未显式捕获的异常。 */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGlobalException(e: Throwable): ApiResponse<Unit> {
        log.error("Unexpected system exception occurred: ", e)
        return ApiResponse.error(500, e.message ?: "Internal Server Error")
    }

    /** 处理业务参数校验异常。 */
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ApiResponse<Unit> {
        log.warn("Business parameter validation failed: ${e.message}")
        return ApiResponse.error(400, e.message ?: "Invalid Request Parameter")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(e: MethodArgumentNotValidException): ApiResponse<Unit> {
        val errorDetails = e.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }
        log.warn("Validation failed: $errorDetails")
        return ApiResponse.error(400, errorDetails)
    }

    /** 处理请求体解析异常。 */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ApiResponse<Unit> {
        log.warn("JSON read error: ${e.message}")
        return ApiResponse.error(400, "Invalid JSON format or parameter type")
    }

    /** 处理缺失请求参数或路径变量。 */
    @ExceptionHandler(ServletRequestBindingException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestBindingException(e: ServletRequestBindingException): ApiResponse<Unit> {
        val detailMessage = e.message ?: "Missing required request parameters"
        log.warn("Request binding failed: $detailMessage")
        return ApiResponse.error(400, detailMessage)
    }

    /** 处理静态资源或路径未命中。 */
    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ApiResponse<Unit> {
        log.warn("Endpoint not found: {} {}", e.httpMethod, e.resourcePath)
        return ApiResponse.error(404, "Endpoint not found: ${e.resourcePath}")
    }

    /** 处理动态路由未命中。 */
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ApiResponse<Unit> {
        log.warn("Endpoint not found: {} {}", e.httpMethod, e.requestURL)
        return ApiResponse.error(404, "Endpoint not found: ${e.requestURL}")
    }

    /** 处理 HTTP 方法不匹配。 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ApiResponse<Unit> {
        log.warn("HTTP method not supported: {}", e.method)
        return ApiResponse.error(405, "Method '${e.method}' not allowed")
    }

    /** 处理不支持的内容类型。 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleMediaTypeNotSupportedException(e: HttpMediaTypeNotSupportedException): ApiResponse<Unit> {
        log.warn("Media type not supported: {}", e.contentType)
        return ApiResponse.error(415, "Unsupported Media Type: ${e.contentType}")
    }

    /** 处理数据库约束冲突。 */
    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ApiResponse<Unit> {
        log.error("Database integrity violation: ", e)
        return ApiResponse.error(409, "Data conflict or database constraint violation")
    }

    /** 处理鉴权失败。 */
    @ExceptionHandler(AuthException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthException(e: AuthException): ApiResponse<Unit> {
        log.warn("Authentication failed: ${e.message}")
        return ApiResponse.error(401, e.message ?: "Unauthorized")
    }

    /** 处理 Spring Security 的认证异常。 */
    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleSpringSecurityAuthException(e: AuthenticationException): ApiResponse<Unit> {
        log.warn("Security authentication failed: ${e.message}")
        return ApiResponse.error(401, e.message ?: "Authentication Failed")
    }

    /** 处理显式抛出的权限不足异常。 */
    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(e: Exception): ApiResponse<Unit> {
        log.warn("Access denied: ${e.message}")
        return ApiResponse.error(403, "Forbidden: You do not have permission to access this resource")
    }

    /** 兜底识别业务层抛出的权限拒绝异常。 */
    @ExceptionHandler(IllegalStateException::class, RuntimeException::class)
    fun handleBusinessForbiddenException(e: Exception): ApiResponse<Unit> {
        val message = e.message
        if (message?.contains("Forbidden", ignoreCase = true) == true ||
            message?.contains("Access denied", ignoreCase = true) == true
        ) {
            log.warn("Business permission check failed: $message")
            return ApiResponse.error(403, "Forbidden: You do not have permission to access this resource")
        }
        throw e
    }
}
