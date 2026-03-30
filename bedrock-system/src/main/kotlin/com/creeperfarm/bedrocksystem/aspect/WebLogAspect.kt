package com.creeperfarm.bedrocksystem.aspect

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class WebLogAspect {

    private val log = LoggerFactory.getLogger(javaClass)

    @Pointcut("execution(public * com.creeperfarm..controller..*.*(..))")
    fun webLog() {
    }

    @Before("webLog()")
    fun doBefore(joinPoint: JoinPoint) {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        val request: HttpServletRequest? = attributes?.request

        log.info("Request: [${request?.method}] ${request?.requestURL} from ${request?.remoteAddr}")
        log.info("Handler: ${joinPoint.signature.declaringTypeName}.${joinPoint.signature.name}")
    }
}
