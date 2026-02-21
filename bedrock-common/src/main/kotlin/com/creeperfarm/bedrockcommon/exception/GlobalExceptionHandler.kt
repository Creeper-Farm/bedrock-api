package com.creeperfarm.bedrockcommon.exception

import com.creeperfarm.bedrockcommon.model.Result
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
import javax.security.sasl.AuthenticationException

/**
 * 全局异常处理器：拦截并统一处理所有 Web 层及业务层抛出的异常
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * [500] 处理所有未明确捕获的系统异常
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGlobalException(e: Throwable): Result<Unit> {
        log.error("Unexpected system exception occurred: ", e)
        return Result.error(500, e.message ?: "Internal Server Error")
    }

    /**
     * [400] 处理业务参数校验异常 (如 IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: IllegalArgumentException): Result<Unit> {
        log.warn("Business parameter validation failed: ${e.message}")
        return Result.error(400, e.message ?: "Invalid Request Parameter")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(e: MethodArgumentNotValidException): Result<Unit> {
        val errorDetails = e.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }
        log.warn("Validation failed: $errorDetails")
        return Result.error(400, errorDetails)
    }

    /**
     * [400] 处理 JSON 解析异常 (请求体格式错误或类型不匹配)
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): Result<Unit> {
        log.warn("JSON read error: ${e.message}")
        return Result.error(400, "Invalid JSON format or parameter type")
    }

    /**
     * [400] 处理缺少必要请求参数或路径变量
     */
    @ExceptionHandler(ServletRequestBindingException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestBindingException(e: ServletRequestBindingException): Result<Unit> {
        val detailMessage = e.message ?: "Missing required request parameters"
        log.warn("Request binding failed: $detailMessage")
        return Result.error(400, detailMessage)
    }

    /**
     * [404] 处理接口不存在异常
     */
    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoResourceFoundException(e: NoResourceFoundException): Result<Unit> {
        log.warn("Endpoint not found: {} {}", e.httpMethod, e.resourcePath)
        return Result.error(404, "Endpoint not found: ${e.resourcePath}")
    }

    /**
     * [404] 处理动态路由未命中的接口异常
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): Result<Unit> {
        log.warn("Endpoint not found: {} {}", e.httpMethod, e.requestURL)
        return Result.error(404, "Endpoint not found: ${e.requestURL}")
    }

    /**
     * [405] 处理请求方法错误 (如：POST 接口用了 GET)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): Result<Unit> {
        log.warn("HTTP method not supported: {}", e.method)
        return Result.error(405, "Method '${e.method}' not allowed")
    }

    /**
     * [415] 处理不支持的内容类型 (如：未设置 application/json)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleMediaTypeNotSupportedException(e: HttpMediaTypeNotSupportedException): Result<Unit> {
        log.warn("Media type not supported: {}", e.contentType)
        return Result.error(415, "Unsupported Media Type: ${e.contentType}")
    }

    /**
     * [409] 处理数据库约束冲突 (如唯一索引冲突、用户名重复)
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): Result<Unit> {
        log.error("Database integrity violation: ", e)
        return Result.error(409, "Data conflict or database constraint violation")
    }

    /**
     * [401] 处理自定义认证异常 (如 JWT 过期、缺失、非法)
     * 注释：拦截我们在拦截器中手动抛出的 AuthException
     */
    @ExceptionHandler(AuthException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthException(e: AuthException): Result<Unit> {
        log.warn("Authentication failed: ${e.message}")
        return Result.error(401, e.message ?: "Unauthorized")
    }

    /**
     * [401] 拦截 Spring Security 的内置异常 (可选)
     * 注释：如果未来你使用了 Security 的原生注解，这个可以捕获对应的异常
     */
    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleSpringSecurityAuthException(e: AuthenticationException): Result<Unit> {
        log.warn("Security authentication failed: ${e.message}")
        return Result.error(401, e.message ?: "Authentication Failed")
    }
}
