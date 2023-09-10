import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.NodeChild
import java.io.File
import java.util.Locale
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.tasks.JacocoReport
import kotlin.math.roundToInt

plugins {
    id("com.android.library")
    id("jacoco")
}

private val limits = mutableMapOf(
    "instruction" to 0.0,
    "branch"      to 0.0,
    "line"        to 0.0,
    "complexity"  to 0.0,
    "method"      to 0.0,
    "class"       to 0.0
)

extra.set("limits", limits)

dependencies {
    "implementation"("org.jacoco:org.jacoco.core:${Versions.jacocoVersion}")
}

project.afterEvaluate {
    val buildTypes = android.buildTypes.map { type -> type.name }
    var productFlavors = android.productFlavors.map { flavor -> flavor.name }

    if (productFlavors.isEmpty()) {
        productFlavors = productFlavors + ""
    }

    productFlavors.forEach { flavorName ->
        buildTypes.forEach { buildTypeName ->
            val sourceName: String
            val sourcePath: String

            if (flavorName.isEmpty()) {
                sourceName = buildTypeName
                sourcePath = buildTypeName
            } else {
                sourceName = "${flavorName}${buildTypeName.capitalized()}"
                sourcePath = "${flavorName}/${buildTypeName}"
            }

            val testTaskName = "test${sourceName.capitalized()}UnitTest"

            registerCodeCoverageTask(
                testTaskName = testTaskName,
                sourceName = sourceName,
                sourcePath = sourcePath,
                flavorName = flavorName,
                buildTypeName = buildTypeName
            )
        }
    }
}

val excludedFiles = mutableSetOf(
    "**/R.class",
    "**/R$*.class",
    "**/*\$ViewInjector*.*",
    "**/*\$ViewBinder*.*",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Factory*",
    "**/*_MembersInjector*",
    "**/*Module*",
    "**/*Component*",
    "**android**",
    "**/BR.class"
)

fun Project.registerCodeCoverageTask(
    testTaskName: String,
    sourceName: String,
    sourcePath: String,
    flavorName: String,
    buildTypeName: String
) {
    tasks.register<JacocoReport>("${testTaskName}Coverage") {
        dependsOn(testTaskName)
        group = "Reporting"
        description = "Generate Jacoco coverage reports on the ${sourceName.capitalize(Locale.ENGLISH)} build."

        val javaDirectories = fileTree(
            "${project.buildDir}/intermediates/classes/${sourcePath}"
        ) { exclude(excludedFiles) }

        val kotlinDirectories = fileTree(
            "${project.buildDir}/tmp/kotlin-classes/${sourcePath}"
        ) { exclude(excludedFiles) }

        val coverageSrcDirectories = listOf(
            "src/main/java",
            "src/$flavorName/java",
            "src/$buildTypeName/java"
        )

        classDirectories.setFrom(files(javaDirectories, kotlinDirectories))
        additionalClassDirs.setFrom(files(coverageSrcDirectories))
        sourceDirectories.setFrom(files(coverageSrcDirectories))
        executionData.setFrom(
            files("${project.buildDir}/jacoco/${testTaskName}.exec")
        )

        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        doLast {
            jacocoTestReport("${testTaskName}Coverage")
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun Project.jacocoTestReport(testTaskName: String) {
    val reportsDirectory = jacoco.reportsDirectory.asFile.get()
    val report = file("$reportsDirectory/${testTaskName}/${testTaskName}.xml")

    logger.lifecycle("Checking coverage results: $report")

    val metrics = report.extractTestsCoveredByType()
    val limits = project.extra["limits"] as Map<String, Double>

    val failures = metrics.filter { entry ->
        entry.value < limits[entry.key]!!
    }.map { entry ->
        "- ${entry.key} coverage rate is: ${entry.value}%, minimum is ${limits[entry.key]}%"
    }

    if (failures.isNotEmpty()) {
        logger.quiet("------------------ Code Coverage Failed -----------------------")
        failures.forEach { logger.quiet(it) }
        logger.quiet("---------------------------------------------------------------")
        throw GradleException("Code coverage failed")
    }

    logger.quiet("------------------ Code Coverage Success -----------------------")
    metrics.forEach { entry ->
        logger.quiet("- ${entry.key} coverage rate is: ${entry.value}%")
    }
    logger.quiet("---------------------------------------------------------------")
}

@Suppress("UNCHECKED_CAST")
fun File.extractTestsCoveredByType(): Map<String, Double> {
    val xmlReader = XmlSlurper().apply {
        setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
    }

    val counterNodes: List<NodeChild> = xmlReader
        .parse(this).parent()
        .children()
        .filter {
            (it as NodeChild).name() == "counter"
        } as List<NodeChild>

    return counterNodes.associate { nodeChild ->
        val type = nodeChild.attributes()["type"].toString().toLowerCase(Locale.ENGLISH)

        val covered = nodeChild.attributes()["covered"].toString().toDouble()
        val missed = nodeChild.attributes()["missed"].toString().toDouble()
        val percentage = ((covered / (covered + missed)) * 10000.0).roundToInt() / 100.0

        Pair(type, percentage)
    }
}
