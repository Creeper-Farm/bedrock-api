description = "bedrock-auth"

dependencies {
    implementation(project(":bedrock-common"))
    implementation(project(":bedrock-user"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.auth0:java-jwt:4.4.0")
}
