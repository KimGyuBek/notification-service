dependencies {

    implementation(project(":notification-adapters:adapter-kafka"))
    implementation(project(":notification-adapters:adapter-persistence"))
    implementation(project(":notification-adapters:adapter-redis"))
    implementation(project(":notification-adapters:adapter-smtp"))
    implementation(project(":notification-adapters:adapter-websocket"))

    implementation(project(":notification-commons"))

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-service"))
    implementation(project(":notification-core:core-domain"))

    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")


    testImplementation("io.jsonwebtoken:jjwt-api")
    testImplementation("io.jsonwebtoken:jjwt-impl")
    testImplementation("io.jsonwebtoken:jjwt-jackson")

    testImplementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb")

}