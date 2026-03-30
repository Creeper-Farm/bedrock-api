plugins {
    id("org.springframework.boot")
}

description = "bedrock-system"

dependencies {
    implementation(project(":bedrock-common"))
    implementation(project(":bedrock-user"))
    implementation(project(":bedrock-auth"))

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("de.codecentric:spring-boot-admin-starter-server:4.0.1")
    implementation("de.codecentric:spring-boot-admin-starter-client:4.0.1")
}
