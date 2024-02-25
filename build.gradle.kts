import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.ksp) apply false
    jacoco
}

allprojects {
    subprojects.forEach { subproject ->
        subproject.tasks.withType(Test::class.java) {
            testLogging.events = setOf(
                TestLogEvent.PASSED,
                TestLogEvent.FAILED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT
            )
        }
    }
}

tasks.register("clean", Delete::class) {
    delete = setOf(layout.buildDirectory)
}

jacoco {
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

project.afterEvaluate {

    tasks.register<JacocoReport>("createTestCoverageReport") {
        dependsOn(subprojects.mapNotNull { it.tasks.findByName("test") })

        group = "Reporting"
        description = "Generate overall Jacoco coverage report for the debug build."

        reports {
            html.required.set(true)
            xml.required.set(true)
            xml.outputLocation.set(file(layout.buildDirectory.file("reports/jacoco/report.xml")))
        }

        val excludes = setOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/ui/preview/*Preview*.*",
            "**/ui/*TopBar*.*",
            "**/core/**",
        )

        val kClasses = subprojects.map { proj ->
            proj.layout.buildDirectory.file("/tmp/kotlin-classes/debug")
        }
        val classes = subprojects.map { proj ->
            proj.layout.buildDirectory.file("/classes/kotlin/main")
        }

        val kotlinClasses = kClasses.map { path -> fileTree(path) { exclude(excludes) } } +
                classes.map { path -> fileTree(path) { exclude(excludes) } }

        classDirectories.setFrom(files(kotlinClasses))
        val sources = subprojects.map { proj -> "${proj.projectDir}/src/main/java" }

        sourceDirectories.setFrom(files(sources))

        val androidExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)
        }.map { proj ->
            proj.layout.buildDirectory.file("/jacoco/testDebugUnitTest.exec")
        }

        val uiExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)
        }.map { proj ->
            fileTree(proj.layout.buildDirectory) {
                include("outputs/**/coverage.ec")
            }
        }

        val kotlinExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin("java-library")
        }.map { proj -> proj.layout.buildDirectory.file("/jacoco/test.exec") }

        executionData.setFrom(files(androidExecutions, kotlinExecutions), uiExecutions)
    }
}
