package com.creeperfarm.bedrockcommon.annotation

/**
 * 身份认证注解
 * 只有经过登录认证（持有有效 JWT）的用户才能访问标注了该注解的类或方法
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Authenticated