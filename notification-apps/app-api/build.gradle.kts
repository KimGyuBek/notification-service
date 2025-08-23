dependencies {

    implementation(project(":notification-adapters:adapter-kafka"))
    implementation(project(":notification-adapters:adapter-persistence"))

    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-web")


}