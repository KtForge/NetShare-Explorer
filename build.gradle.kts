import org.gradle.api.tasks.testing.logging.TestLogEvent

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

tasks.register("debugUiTest") {
    val subprojectTasks = subprojects.filter { subproject ->
        subproject.plugins.hasPlugin(Plugins.androidLibrary)
    }.map { subproject ->
        "${subproject.path}:${subproject.tasks.findByName("connectedDebugAndroidTest")?.name}"
    }

    dependsOn(subprojectTasks)
}
