dependencies {

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-domain"))
    implementation(project(":notification-commons"))

    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

}