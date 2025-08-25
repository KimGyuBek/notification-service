dependencies {
    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("it.ozimov:embedded-redis:0.7.2")

}