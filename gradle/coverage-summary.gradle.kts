val modules = listOf(
    "notification-core:core-domain",
    "notification-core:core-service",
    "notification-core:core-port",
    "notification-commons",
    "notification-adapters:adapter-persistence",
    "notification-adapters:adapter-redis",
    "notification-adapters:adapter-kafka",
    "notification-apps:app-api"
)

fun parseCoverage(xmlFile: File): Map<String, String> {
    if (!xmlFile.exists()) {
        return emptyMap()
    }

    val parser = groovy.xml.XmlParser()
    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
    parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

    val xml = parser.parse(xmlFile)
    val counters = (xml as groovy.util.Node).get("counter") as List<*>

    // 모든 counter 타입을 기본값으로 초기화
    val coverage = mutableMapOf(
        "INSTRUCTION" to "0.0%",
        "BRANCH" to "0.0%",
        "LINE" to "0.0%",
        "METHOD" to "0.0%",
        "CLASS" to "0.0%"
    )

    counters.forEach { counter ->
        val node = counter as groovy.util.Node
        val type = node.attribute("type") as String
        val missed = (node.attribute("missed") as String).toInt()
        val covered = (node.attribute("covered") as String).toInt()
        val total = missed + covered
        val percentage = if (total > 0) String.format("%.1f%%", covered * 100.0 / total) else "0.0%"

        coverage[type] = percentage
    }

    return coverage
}

/*콘솔 출력*/
tasks.register("printCoverageSummary") {
    group = "verification"
    description = "Print module-by-module coverage summary to console"

    // jacocoTestReport가 실행되면 그 이후에 실행 (강제 의존성 제거)
    mustRunAfter(subprojects.mapNotNull { it.tasks.findByName("jacocoTestReport") })

    doLast {
        println("\n" + "=".repeat(100))
        println("각 모듈별 Test Coverage")
        println("=".repeat(100))
        println(
            String.format(
                "%-25s %12s %12s %12s %12s %12s",
                "MODULE", "INSTRUCTION", "BRANCH", "LINE", "METHOD", "CLASS"
            )
        )
        println("-".repeat(100))

        modules.forEach { modulePath ->
            val module = project.findProject(":$modulePath") ?: return@forEach
            val xmlFile =
                module.layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
                    .get().asFile
            val coverage = parseCoverage(xmlFile)

            if (coverage.isEmpty()) {
                return@forEach
            }

            println(
                String.format(
                    "%-25s %12s %12s %12s %12s %12s",
                    module.name,
                    coverage["INSTRUCTION"] ?: "N/A",
                    coverage["BRANCH"] ?: "N/A",
                    coverage["LINE"] ?: "N/A",
                    coverage["METHOD"] ?: "N/A",
                    coverage["CLASS"] ?: "N/A"
                )
            )
        }

        println("=".repeat(100) + "\n")
    }
}

/*md 문서 생성*/
tasks.register("printCoverageSummaryMarkdown") {
    group = "verification"
    description = "Generate module-by-module coverage summary in Markdown format to file"

    val outputFile = layout.buildDirectory.file("reports/coverage-summary.md")

    outputs.file(outputFile)

    doLast {
        val content = buildString {
            appendLine("## Code Coverage Report (모듈 별)")
            appendLine()
            appendLine("| Module | Instruction | Branch | Line | Method | Class |")
            appendLine("|--------|-------------|--------|------|--------|-------|")

            modules.forEach { modulePath ->
                val module = project.findProject(":$modulePath") ?: return@forEach
                val xmlFile =
                    module.layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
                        .get().asFile
                val coverage = parseCoverage(xmlFile)

                if (coverage.isEmpty()) {
                    return@forEach
                }
                appendLine("| ${module.name} | ${coverage["INSTRUCTION"] ?: "N/A"} | ${coverage["BRANCH"] ?: "N/A"} | ${coverage["LINE"] ?: "N/A"} | ${coverage["METHOD"] ?: "N/A"} | ${coverage["CLASS"] ?: "N/A"} |")
            }

            appendLine()
            appendLine(
                "Generated: ${
                    java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                }"
            )
        }

        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(content)
        }

        println(content)
    }
}
