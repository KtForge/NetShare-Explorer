import org.gradle.api.tasks.testing.logging.TestLogEvent

// Change to open PR
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlin.jvm") apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    kotlin("plugin.serialization") version "1.9.10"
    jacoco
}

buildscript {
    dependencies {
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
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
        subproject.plugins.hasPlugin(Plugins.androidLibrary) || subproject.plugins.hasPlugin(Plugins.javaLibrary)
    }.map { subproject ->
        if (subproject.plugins.hasPlugin(Plugins.androidLibrary)) {
            "${subproject.path}:${subproject.tasks.findByName("testDebugUnitTest")?.name}"
        } else {
            "${subproject.path}:${subproject.tasks.findByName("test")?.name}"
        }
    }

    dependsOn(subprojectTasks)
}

tasks.register("debugUnitTestCoverage") {
    val subprojectTasks = subprojects.filter { subproject ->
        subproject.plugins.hasPlugin(Plugins.androidLibrary) || subproject.plugins.hasPlugin(Plugins.javaLibrary)
    }.mapNotNull { subproject ->
        if (subproject.plugins.hasPlugin(Plugins.androidLibrary)) {
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
        subproject.plugins.hasPlugin(Plugins.androidLibrary)
    }.map { subproject ->
        "${subproject.path}:${subproject.tasks.findByName("connectedDebugAndroidTest")?.name}"
    }

    dependsOn(subprojectTasks)
}

jacoco {
    reportsDirectory.set(layout.buildDirectory.dir("/reports/jacoco"))
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
            "androidx/**/*.*",
            "**/*ViewInjector*.*",
            "**/*Dagger*.*",
            "**/*MembersInjector*.*",
            "**/*_Factory.*",
            "**/*_Provide*Factory*.*",
            "**/*_ViewBinding*.*",
            "**/AutoValue_*.*",
            "**/R2.class",
            "**/R2$*.class",
            "**/*Directions$*",
            "**/*Directions.*",
            "**/*Binding.*",
            "**/core/**"
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
            proj.plugins.hasPlugin(Plugins.androidLibrary)
        }.map { proj ->
            val path = "${proj.buildDir}/jacoco/testDebugUnitTest.exec"
            println("Android unit test report: $path")

            path
        }

        val uiExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin(Plugins.androidLibrary)
        }.map { proj ->
            fileTree(proj.buildDir) {
                include("outputs/**/coverage.ec")
            }
            //val path = "${proj.buildDir}/outputs/**/coverage.ec"
            //println("UI unit tests report: $path")
//
            //path
        }

        val kotlinExecutions = subprojects.filter { proj ->
            proj.plugins.hasPlugin(Plugins.javaLibrary)
        }.map { proj ->
            val path = "${proj.buildDir}/jacoco/test.exec"
            println("Kotlin unit tests report: $path")

            path
        }

        executionData.setFrom(files(androidExecutions, kotlinExecutions), uiExecutions)
    }
}
