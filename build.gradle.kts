import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.BufferedReader
import java.io.InputStreamReader

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlin.jvm") apply false
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
    val subprojectTasks = subprojects.mapNotNull { subproject ->
        if (subproject.plugins.hasPlugin(Plugins.androidLibrary)) {
            "${subproject.path}:${subproject.tasks.findByName("testDebugUnitTest")?.name}"
        } else if (subproject.plugins.hasPlugin(Plugins.javaLibrary)) {
            "${subproject.path}:${subproject.tasks.findByName("test")?.name}"
        } else {
            null
        }
    }

    dependsOn(subprojectTasks)
}

tasks.register("debugUiTest") {
    val subprojectTasks = subprojects.mapNotNull { subproject ->
        if (subproject.plugins.hasPlugin(Plugins.androidLibrary)) {
            "${subproject.path}:${subproject.tasks.findByName("connectedDebugAndroidTest")?.name}"
        } else {
            null
        }
    }

    dependsOn(subprojectTasks)
}

tasks.register("cucumber") {

    group = "verification"
    dependsOn(":cucumber:runCucumber")
    finalizedBy(":cucumber:generateCucumberReports")
}
