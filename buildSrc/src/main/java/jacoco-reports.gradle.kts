plugins {
    id("com.android.library")
    jacoco
}

android {
    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
    }
}

val excludedFiles = mutableSetOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
)

tasks.register<JacocoReport>("testDebugUnitTestCoverage") {
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    group = "Reporting"
    description = "Generate Jacoco coverage reports on the debug build."

    val kotlinDirectories = fileTree(
        "${project.buildDir}/tmp/kotlin-classes/debug"
    ) { exclude(excludedFiles) }

    classDirectories.setFrom(files(kotlinDirectories))
    executionData.setFrom(
        fileTree(project.buildDir) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/**/coverage.ec"
            )
        }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
