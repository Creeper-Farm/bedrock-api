plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
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
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 框架校验
    api("org.springframework.boot:spring-boot-starter-validation")

    // Exposed 相关
    api("org.jetbrains.exposed:exposed-core:1.0.0")
    api("org.jetbrains.exposed:exposed-jdbc:1.0.0")
    api("org.jetbrains.exposed:exposed-dao:1.0.0")
    api("org.jetbrains.exposed:exposed-java-time:1.0.0")
    api("org.jetbrains.exposed:exposed-spring-boot-starter:1.0.0")
    api("org.jetbrains.exposed:exposed-migration-core:1.0.0")
    api("org.jetbrains.exposed:exposed-migration-jdbc:1.0.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
