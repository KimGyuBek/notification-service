dependencies {

    implementation(project(":notification-core:core-port"))
    implementation(project(":notification-core:core-domain"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework:spring-tx")
    
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

}