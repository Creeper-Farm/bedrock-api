plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.creeperfarm"
version = "0.0.1-SNAPSHOT"
description = "bedrock-common"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))

    // 基础核心与 Web
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aop")

    // Kotlin 增强
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 数据库
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.postgresql:postgresql")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 框架校验
    api("org.springframework.boot:spring-boot-starter-validation")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
