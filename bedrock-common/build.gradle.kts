description = "bedrock-common"

dependencies {
    api("org.springframework.boot:spring-boot-starter-webmvc")
    api("org.springframework.boot:spring-boot-starter-jackson")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aspectj")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-validation")

    api("org.jetbrains.kotlin:kotlin-reflect")
    api("tools.jackson.module:jackson-module-kotlin")
    api("org.postgresql:postgresql")

    api("org.jetbrains.exposed:exposed-core:1.0.0")
    api("org.jetbrains.exposed:exposed-jdbc:1.0.0")
    api("org.jetbrains.exposed:exposed-dao:1.0.0")
    api("org.jetbrains.exposed:exposed-java-time:1.0.0")
    api("org.jetbrains.exposed:spring-transaction:1.0.0")
    api("org.jetbrains.exposed:exposed-migration-core:1.0.0")
    api("org.jetbrains.exposed:exposed-migration-jdbc:1.0.0")
}
