dependencies {

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-domain"))
    implementation(project(":notification-commons"))

    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

}