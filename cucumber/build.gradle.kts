import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidTest)
    id(Plugins.kotlinAndroid)
    id(Plugins.cucumberReporting) version Versions.cucumberReporting
}

android {
    namespace = "com.msd.network.explorer.test"
    compileSdk = Configuration.compileSdk

    defaultConfig {
        minSdk = Configuration.minSdk
        testApplicationId = "com.msd.network.explorer.test"

        testInstrumentationRunner = "com.msd.network.explorer.test.ExplorerCucumberTestRunner"
    }

    compileOptions {
        sourceCompatibility = Configuration.javaVersion
        targetCompatibility = Configuration.javaVersion
    }

    targetProjectPath = ":app"
}

dependencies {

    implementation(project(":core:uitest"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:presentation"))
    implementation(project(":core:tracking"))

    implementation(project(":data:smb_data"))
    implementation(project(":data:explorer_data"))

    implementation(project(":domain:smb"))
    implementation(project(":domain:explorer"))

    implementation(project(":feature:main"))
    implementation(project(":feature:edit"))
    implementation(project(":feature:explorer"))

    implementation(platform(Dependencies.firebaseBom))
    implementation(Dependencies.firebaseAnalytics)
    implementation(Dependencies.firebaseCrashlytics)

    implementation(Dependencies.roomRuntime)

    implementation(Dependencies.smbj)

    implementation(Dependencies.cucumberAndroid)
    implementation(Dependencies.cucumberHilt)

    implementation(Dependencies.daggerHiltAndroid)
    implementation(Dependencies.daggerHiltAndroidTesting)
    kapt(Dependencies.daggerHiltAndroidCompiler)
}

var record: Boolean = false

afterEvaluate {
    tasks.getByName("generateCucumberReports") {
        dependsOn("downloadCucumberReports")
        finalizedBy("compressCucumberReport")
    }
    tasks.getByName("mergeDebugAssets") {
        dependsOn("setRecordingMode")
    }
}

// Run before generating cucumber reports
tasks.create("downloadCucumberReports") {
    group = "Verification"
    description = "Downloads the rich Cucumber report files from the connected device"

    doLast {
        val localReportPath = File(buildDir, "reports/cucumber")
        println("local report path: $localReportPath")

        if (!localReportPath.exists()) {
            localReportPath.mkdirs()
        }
        val localPath = File(localReportPath, "cucumber.json")

        if (!localReportPath.exists()) {
            throw GradleException("Could not create $localReportPath")
        }

        // Pull cucumber.json file
        exec { commandLine(getAdbPath(), "pull", getCucumberJsonDevicePath(), localPath) }
    }
}

// Run after generating cucumber reports

tasks.register<Zip>("compressCucumberReport") {

    doLast {
        archivesName.set("cucumber_report")
        destinationDirectory.set(file(buildDir))
        from(files("$buildDir/reports/cucumber/cucumber.html"))
    }
}

// Run before mergeDebugAssets

tasks.register<WriteProperties>("setRecordingMode") {

    record = if (project.hasProperty("record")) {
        (project.property("record") as String).toBoolean()
    } else {
        false
    }

    outputFile = file("src/main/assets/config/recording.properties")
    println("Recording mode: $record")
    property("record", record)
}

tasks.register("installTestApp") {
    dependsOn("deleteDeviceCucumberReportsAndLogs", "installDebug", ":app:installDebug")
}

// Delete previous cucumber reports

tasks.register("deleteDeviceCucumberReportsAndLogs") {

    doLast {
        println("Deleting previous cached report...")
        exec { commandLine(getAdbPath(), "shell", "rm -f ${getCucumberJsonDevicePath()}") }
        exec { commandLine(getAdbPath(), "shell", "rm -f -r ${getCucumberLogsDevicePath()}") }
    }
}

fun getCucumberJsonDevicePath(): String {
    return "/data/user/0/com.msd.network.explorer/cache/reports/cucumber/cucumber.json"
}

// Cucumber report plugin configuration

cucumberReports {
    outputDir = file(buildDir.path + "/reports/cucumber/cucumber.html")
    buildId = "0"
    reports = files(buildDir.path + "/reports/cucumber/cucumber.json")
}

tasks.register("cucumber") {

    group = "verification"
    dependsOn("grantRootAccess", "runCucumber")
    finalizedBy("generateCucumberReports", "downloadLogs")
}

// Grant root access to allow pulling the reports and log files

tasks.register("grantRootAccess") {

    doLast {
        val adb = getAdbPath()

        // Enable root on the device to pull files from app's folder
        exec { commandLine(adb, "root") }
        // Wait for the device to be restarted as root
        exec { commandLine(adb, "wait-for-device") }
    }
}

// Run when invoking cucumber task

tasks.register("runCucumber") {

    dependsOn("installTestApp")

    doLast {
        val tagsParameter = if (project.hasProperty("tags")) {
            val tags = project.property("tags")
            println("Running tests tagged with: $tags")
            "-e tags $tags"
        } else {
            println("No tags provided, running all tests")
            ""
        }

        exec {
            commandLine(
                getAdbPath(),
                "shell",
                "am",
                "instrument",
                "-w",
                tagsParameter,
                "com.msd.network.explorer.test/.ExplorerCucumberTestRunner"
            )
        }
    }
}

// Run after generating cucumber reports

tasks.register("downloadLogs") {
    doLast {
        if (record) {
            // Define the path to the local logs directory
            val localLogsDirectory = file("${projectDir.absolutePath}/src/main/assets/")

            // Create the logs directory if it doesn't exist
            if (!localLogsDirectory.exists()) {
                localLogsDirectory.mkdirs()
            }

            // Pull all files inside /data/user/0/packageId/cache/logs folder
            exec {
                commandLine(getAdbPath(), "pull", getCucumberLogsDevicePath(), localLogsDirectory)
            }
        } else {
            println("Not recording, skipping logs...")
        }
    }
}

fun getCucumberLogsDevicePath(): String {
    return "/data/user/0/com.msd.network.explorer/cache/logs/"
}

fun getAdbPath(): String {
    val adb = "${System.getenv("ANDROID_HOME")}/platform-tools/adb"
    if (adb.isEmpty()) {
        throw GradleException("Could not detect adb path")
    }
    return adb
}
