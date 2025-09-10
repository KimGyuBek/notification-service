dependencies {

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-domain"))
    
    implementation(project(":notification-commons"))

    implementation("org.springframework:spring-websocket")
    implementation("org.springframework:spring-messaging")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.slf4j:slf4j-api")
    
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

}