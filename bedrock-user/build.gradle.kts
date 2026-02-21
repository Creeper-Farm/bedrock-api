plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.creeperfarm"
version = "0.0.1-SNAPSHOT"
description = "bedrock-user"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 1. 引入版本管理 (BOM)，确保版本与其它模块严格一致
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))

    // 2. 核心依赖：引入你的 common 模块
    // 这样 user 模块就能直接使用 Result, ExceptionHandler, 以及 common 里的 JPA/Postgres 依赖
    implementation(project(":bedrock-common"))

    // 3. Kotlin 必备
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 4. 测试相关
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.security:spring-security-crypto")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
