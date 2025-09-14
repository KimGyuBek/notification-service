dependencies {

    implementation(project(":notification-core:core-domain"))

    implementation("io.jsonwebtoken:jjwt-api")
    implementation("io.jsonwebtoken:jjwt-impl")
    implementation("io.jsonwebtoken:jjwt-jackson")


    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-web")

}