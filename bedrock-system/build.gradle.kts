plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.creeperfarm"
version = "0.0.1-SNAPSHOT"
description = "bedrock-system"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":bedrock-common"))
    implementation(project(":bedrock-user"))

    // 基础核心
    implementation("org.springframework.boot:spring-boot-starter")

    // Web 与 AOP
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // 数据库
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Kotlin 序列化增强
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Spring Boot Admin Server
    implementation("de.codecentric:spring-boot-admin-starter-server:3.4.1")
    // Client 端（让自己监控自己）
    implementation("de.codecentric:spring-boot-admin-starter-client:3.4.1")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
