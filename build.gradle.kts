import org.apache.commons.io.filefilter.WildcardFileFilter
import org.apache.tools.ant.DirectoryScanner
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.FileFilter

// Test
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
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

fun getListOfReports(): Set<File> {
    val scanner = DirectoryScanner()
    scanner.setIncludes(arrayOf("**/reports/coverage/**/*.xml", "**/reports/jacoco/**/*.xml"))
    scanner.setExcludes(arrayOf("build/"))
    scanner.setBasedir(projectDir.absolutePath)
    scanner.isCaseSensitive = false
    scanner.scan()

    val files: Array<String> = scanner.includedFiles
    return files.map { filePath ->
        File(filePath)
    }.toSet()
}

tasks.register<Delete>("deleteIndividualJacocoReports") {
    getListOfReports().forEach { file ->
        println("Deleting ${file.absolutePath}")
    }
    delete = getListOfReports()
}

tasks.register<JacocoReport>("createTestCoverageReport") {
    dependsOn("debugUnitTestCoverage")
    finalizedBy("deleteIndividualJacocoReports")

    group = "Reporting"
    description = "Generate overall Jacoco coverage report for the debug build."

    reports {
        html.required.set(true)
        xml.required.set(true)
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
        val path = "${proj.buildDir}/jacoco/testDebugUnitTest.exec"
        proj.plugins.hasPlugin(Plugins.androidLibrary) && File(path).exists()
    }.map { proj ->
        "${proj.buildDir}/jacoco/testDebugUnitTest.exec"
    }

    val uiExecutions = subprojects.map { proj ->
        val path = "${proj.buildDir}/outputs/code_coverage/debugAndroidTest/connected"

        val emulatorDirectory = File(path).listFiles()?.firstOrNull()
        val executionFile = if (emulatorDirectory != null) {
            File(emulatorDirectory.absolutePath, "coverage.ec")
        } else {
            null
        }

        executionFile
    }.mapNotNull { executionFile -> executionFile?.absolutePath }

    val kotlinExecutions = subprojects.filter { proj ->
        val path = "${proj.buildDir}/jacoco/test.exec"
        proj.plugins.hasPlugin(Plugins.javaLibrary) && File(path).exists()
    }.map { proj ->
        "${proj.buildDir}/jacoco/test.exec"
    }

    executionData.setFrom(files(androidExecutions, uiExecutions, kotlinExecutions))
}

