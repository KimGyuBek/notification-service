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
                    // DTO 및 전송 객체
                    "**/*Command.class",
                    "**/*Query.class",
                    "**/*Response.class",
                    "**/*Request.class",
                    "**/*ApiResponse.class",
                    "**/*Details.class",

                    // 데이터 클래스
                    "**/*Meta.class",
                    "**/*Doc.class",
                    "**/*Entity.class",
                    "**/*Model.class",
                    "**/*Preview.class",

                    // Enum
                    "**/*Type.class",
                    "**/*Status.class",
                    "**/*Event.class",
                    "**/*Code.class",

                    // 인터페이스
                    "**/*Port.class",
                    "**/*UseCase.class",
                    "**/*Projection.class",

                    // 설정 및 모듈
                    "**/*Config.class",
                    "**/*Properties.class",
                    "**/*Module.class",
                    "**/*Application.class",

                    // 예외
                    "**/*Exception.class",

                    // 매퍼 및 레포지토리
                    "**/adapter/persistence/**/repository/**/*.class",
                    "**/adapter/persistence/**/*Repository.class",

                    // 인프라
                    "**/filter/**/*.class",
                    "**/interceptor/**/*.class",

                    // 기타
                    "**/base/BaseEntity.class"
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
