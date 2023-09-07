import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.ArrayList


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

    implementation(project(":data:smb_data"))
    implementation(project(":data:explorer_data"))

    implementation(project(":domain:smb"))
    implementation(project(":domain:explorer"))

    implementation(project(":feature:main"))
    implementation(project(":feature:edit"))
    implementation(project(":feature:explorer"))

    implementation(Dependencies.roomRuntime)

    implementation(Dependencies.smbj)

    implementation(Dependencies.cucumberAndroid)
    implementation(Dependencies.cucumberHilt)

    implementation(Dependencies.daggerHiltAndroid)
    implementation(Dependencies.daggerHiltAndroidTesting)
    kapt(Dependencies.daggerHiltAndroidCompiler)
}

afterEvaluate {
    tasks.getByName("generateCucumberReports") {
        dependsOn("downloadCucumberReports")
        finalizedBy("compressCucumberReport")
    }
}

cucumberReports {
    outputDir = file(buildDir.path + "/reports/cucumber/cucumber.html")
    buildId = "0"
    reports = files(buildDir.path + "/reports/cucumber/cucumber.json")
}

tasks.register("installTestApp") {
    dependsOn("deleteDeviceCucumberReports", "installDebug", ":app:installDebug")
}

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

        val adb = getAdbPath()
        exec {
            commandLine(
                adb,
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

        val adb = getAdbPath()

        exec {
            commandLine(
                adb,
                "shell",
                "su 0 cat ${getCucumberJsonDevicePath()} > ${getCucumberJsonSdCardDevicePath()}"
            )
        }
        exec { commandLine(adb, "pull", getCucumberJsonSdCardDevicePath(), localPath) }
    }
}

tasks.register("downloadLogs") {
    doLast {
        val localLogsPath = File(projectDir, "src/main/assets/logs")
        println("local report path: $localLogsPath")

        if (!localLogsPath.exists()) {
            localLogsPath.mkdirs()
        }

        val adb = getAdbPath()

        val stdout = ByteArrayOutputStream()
        exec {
            commandLine(
                adb,
                "shell",
                "su 0 ls ${getCucumberLogsDevicePath()}"
            )
            standardOutput = stdout
        }
        val files = String(stdout.toByteArray())
        val filesList = files.lines().filter { it.isNotEmpty() }.forEach { line ->
            println("Line: $line")
            exec {
                commandLine(
                    adb,
                    "shell",
                    "su 0 cat ${getCucumberLogsDevicePath()}$line > ${getCucumberLogSdCardDevicePath()}$line"
                )
            }
            exec { commandLine(adb, "pull", getCucumberLogSdCardDevicePath() + line, File(localLogsPath, line)) }
        }
    }
}

tasks.register<Zip>("compressCucumberReport") {

    archivesName.set("cucumber_report")
    destinationDirectory.set(file(buildDir))
    from(files("$buildDir/reports/cucumber/cucumber.html"))
}

tasks.register("deleteDeviceCucumberReports") {
    val adb = getAdbPath()

    println("Deleting previous sdcard report...")
    exec { commandLine(adb, "shell", "rm -f ${getCucumberJsonSdCardDevicePath()}") }

    println("Deleting previous cached report...")
    exec { commandLine(adb, "shell", "su 0 rm -f ${getCucumberJsonDevicePath()}") }
}

// ==================================================================
// Utility methods
// ==================================================================

/**
 * Utility method to get the full ADB path
 * @return the absolute ADB path
 */
fun getAdbPath(): String {
    val adb = "${System.getenv("ANDROID_HOME")}/platform-tools/adb"
    if (adb.isEmpty()) {
        throw GradleException("Could not detect adb path")
    }
    return adb
}

fun getCucumberLogsDevicePath(): String {
    return "/data/user/0/com.msd.network.explorer/cache/logs/"
}

fun getCucumberLogSdCardDevicePath(): String {
    return "/sdcard/"
}

/**
 * The path which is used to store the Cucumber files.
 * @return
 */
fun getCucumberJsonDevicePath(): String {
    return "/data/user/0/com.msd.network.explorer/cache/reports/cucumber/cucumber.json"
}

/**
 * The path which is used to store the Cucumber files.
 * @return
 */
fun getCucumberJsonSdCardDevicePath(): String {
    return "/sdcard/cucumber.json"
}