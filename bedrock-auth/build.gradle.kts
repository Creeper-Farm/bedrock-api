plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

group = "com.creeperfarm"
version = "0.0.1-SNAPSHOT"
description = "bedrock-auth"

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

    // 1. 解决 StringRedisTemplate 报错
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 2. 解决 BCryptPasswordEncoder 报错
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
