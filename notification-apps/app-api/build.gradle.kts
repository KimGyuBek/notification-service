dependencies {

    implementation(project(":notification-adapters:adapter-kafka"))
    implementation(project(":notification-adapters:adapter-persistence"))
    implementation(project(":notification-adapters:adapter-redis"))
    implementation(project(":notification-adapters:adapter-smtp"))

    implementation(project(":notification-commons"))

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-service"))
    implementation(project(":notification-core:core-domain"))

    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")


    testImplementation("io.jsonwebtoken:jjwt-api")
    testImplementation("io.jsonwebtoken:jjwt-impl")
    testImplementation("io.jsonwebtoken:jjwt-jackson")

    testImplementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb")

}