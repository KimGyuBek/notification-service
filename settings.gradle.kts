rootProject.name = "notification-service"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
        maven {
            url = uri("https://maven.springframework,org/release")
        }
        maven {
            url = uri("https://maven.restlet.com")
        }
    }

    include("notification-apps:app-api")
    include("notification-adapters:adapter-persistence")
    include("notification-adapters:adapter-kafka")
    include("notification-core:core-domain")
    include("notification-core:core-service")
    include("notification-core:core-port")
}
