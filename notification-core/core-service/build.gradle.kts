dependencies {

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-domain"))
    implementation(project(":notification-commons"))


    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

}