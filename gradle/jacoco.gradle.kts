apply(plugin = "jacoco")

configure<JacocoPluginExtension> {
    toolVersion = "0.8.11"
}

tasks.named<Test>("test") {
    finalizedBy("jacocoTestReport")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/*Application.class",
                    "**/*Config.class",
                    "**/*Properties.class",
                    "**/*Module.class",
                    "**/dto/**/*Request.class",
                    "**/dto/**/*Response.class",
                    "**/dto/**/*Command.class",
                    "**/dto/**/*ApiResponse.class",
                    "**/*Exception.class",
                    "**/ErrorCode.class",
                    "**/*Type.class",
                    "**/*Status.class",
                    "**/port/**/*Port.class",
                    "**/port/**/*UseCase.class"
                )
            }
        })
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}
