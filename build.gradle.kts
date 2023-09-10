import com.android.build.gradle.internal.coverage.JacocoReportTask
import org.gradle.api.tasks.testing.logging.TestLogEvent

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlin.jvm") apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    jacoco
}

jacoco {
    toolVersion = Versions.jacocoVersion
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
        subproject.plugins.hasPlugin(Plugins.androidLibrary) // || subproject.plugins.hasPlugin(Plugins.javaLibrary)
    }.mapNotNull { subproject ->
        if (subproject.plugins.hasPlugin(Plugins.androidLibrary)) {
            val taskName = subproject.tasks.findByName("testDebugUnitTestCoverage")?.name
            "${subproject.path}:$taskName".takeUnless { taskName.isNullOrEmpty() }
        } else {
            null // "${subproject.path}:${subproject.tasks.findByName("test")?.name}"
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

tasks.register<JacocoReport>("jacocoCombinedTestReports") {
    group = "Verification"
    description =
        "Creates JaCoCo test coverage report for Unit and Instrumented Tests (combined) on the Debug build"

    dependsOn("debugUnitTestCoverage")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // Files to exclude:
    // Generated classes, platform classes, etc.
    val excludedFiles = setOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    // generated classes
    classDirectories.setFrom(
        files(
            fileTree("$buildDir/intermediates/classes/debug") { exclude(excludedFiles) },
            fileTree("$buildDir/tmp/kotlin-classes/debug") { exclude(excludedFiles) }
        )
    )
    val coverageSrcDirectories = listOf("src/main/java")

    // sources
    sourceDirectories.setFrom(files(coverageSrcDirectories))
    // Output and existing data
    // Combine Unit test and Instrumented test reports
    executionData.setFrom(
        files(fileTree(buildDir) {
            include(
                setOf(
                    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                    "outputs/code_coverage/debugAndroidTest/connected/*coverage.ec"
                )
            )
        }
        )
    )
}
