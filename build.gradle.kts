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

tasks.register("debugUnitTest") {
    val subprojectTasks = subprojects.filter { subproject ->
        subproject.plugins.hasPlugin(libs.plugins.android.library.get().pluginId) ||
                subproject.plugins.hasPlugin("java-library")
    }.map { subproject ->
        if (subproject.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)) {
            "${subproject.path}:${subproject.tasks.findByName("testDebugUnitTest")?.name}"
        } else {
            "${subproject.path}:${subproject.tasks.findByName("test")?.name}"
        }
    }

    dependsOn(subprojectTasks)
}

tasks.register("debugUnitTestCoverage") {
    val subprojectTasks = subprojects.filter { subproject ->
        subproject.plugins.hasPlugin(libs.plugins.android.library.get().pluginId) ||
                subproject.plugins.hasPlugin("java-library")
    }.mapNotNull { subproject ->
        if (subproject.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)) {
            val taskName = subproject.tasks.findByName("testDebugUnitTestCoverage")?.name
            "${subproject.path}:$taskName".takeUnless { taskName.isNullOrEmpty() }
        } else {
            "${subproject.path}:${subproject.tasks.findByName("test")?.name}"
        }
    }

    dependsOn(subprojectTasks)
}

tasks.register("debugUiTest") {
    val subprojectTasks = subprojects.filter { subproject ->
        subproject.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)
    }.map { subproject ->
        "${subproject.path}:${subproject.tasks.findByName("connectedDebugAndroidTest")?.name}"
    }

    dependsOn(subprojectTasks)
}

jacoco {
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.register("deleteIndividualJacocoReports") {

    doLast {
        val report1Regex = ".*testDebugUnitTestCoverage.xml".toRegex()
        val report2Regex = ".*connected.*report.xml".toRegex()
        val report3Regex = ".*jacoco.*test.*jacocoTestReport.xml".toRegex()

        val filesToDelete = File(".").walkTopDown().filter { file ->
            report1Regex.containsMatchIn(file.absolutePath) ||
                    report2Regex.containsMatchIn(file.absolutePath) ||
                    report3Regex.containsMatchIn(file.absolutePath)
        }.toSet()

        filesToDelete.forEach { file ->
            println("Deleting $file")
            file.delete()
        }
    }
}

project.afterEvaluate {

    tasks.register<JacocoReport>("createTestCoverageReport") {
        dependsOn("debugUnitTestCoverage")
        finalizedBy("deleteIndividualJacocoReports")

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
            "${proj.buildDir}/tmp/kotlin-classes/debug"
        }
        val classes = subprojects.map { proj ->
            "${proj.buildDir}/classes/kotlin/main"
        }
        val kotlinClasses = kClasses.map { path ->
            fileTree(path) { exclude(excludes) }
        } + classes.map { path ->
            fileTree(path) { exclude(excludes) }
        }

        classDirectories.setFrom(files(kotlinClasses))
        val sources = subprojects.map { proj ->
            "${proj.projectDir}/src/main/java"
        }

        sourceDirectories.setFrom(files(sources))

        val androidExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)
        }.map { proj ->
            val path = "${proj.buildDir}/jacoco/testDebugUnitTest.exec"
            println("Android unit test report: $path")

            path
        }

        val uiExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)
        }.map { proj ->
            fileTree(proj.buildDir) {
                include("outputs/**/coverage.ec")
            }
        }

        val kotlinExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin("java-library")
        }.map { proj ->
            val path = "${proj.buildDir}/jacoco/test.exec"
            println("Kotlin unit tests report: $path")

            path
        }

        executionData.setFrom(files(androidExecutions, kotlinExecutions), uiExecutions)
    }
}
