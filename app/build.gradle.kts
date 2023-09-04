import java.util.Properties

plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.daggerHiltAndroid)
    id("com.github.spacialcircumstances.gradle-cucumber-reporting") version "0.1.25"
}

val properties = Properties()
val propertiesFile = File("version")

var major = 0
var minor = 0
var patch = 0
var build = 0

if (propertiesFile.canRead()) {
    properties.load(propertiesFile.inputStream())

    major = (properties["version.major"] as String).toInt()
    minor = (properties["version.minor"] as String).toInt()
    patch = (properties["version.patch"] as String).toInt()
    build = (properties["version.build"] as String).toInt()
}

android {
    namespace = Configuration.namespace
    compileSdk = Configuration.compileSdk


    signingConfigs {
        create("release") {
            if (System.getenv("CI").toBoolean()) {
                storeFile = file(System.getenv()["CM_KEYSTORE_PATH"] as String)
                storePassword = System.getenv()["CM_KEYSTORE_PASSWORD"]
                keyAlias = System.getenv()["CM_KEY_ALIAS"]
                keyPassword = System.getenv()["CM_KEY_PASSWORD"]
            } else {
                val properties = Properties()
                val propertiesFile = File("key.properties")
                if (propertiesFile.canRead()) {
                    properties.load(propertiesFile.inputStream())

                    keyAlias = properties["key.alias"] as String
                    keyPassword = properties["key.alias.password"] as String
                    storeFile = file(properties["key.store.file"] as String)
                    storePassword = properties["key.store.password"] as String
                }
            }
        }
    }

    defaultConfig {
        applicationId = Configuration.namespace
        testApplicationId = Configuration.namespace + ".test"
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = major.times(10000) + minor.times(1000) + patch.times(100) + build
        versionName = "$major.$minor.$patch"

        testInstrumentationRunner = "com.msd.network.explorer.test.ExplorerCucumberTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "false"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = Configuration.javaVersion
        targetCompatibility = Configuration.javaVersion
    }
    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.kotlinCompilerExtensionVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

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

    implementation(platform(Dependencies.kotlinBom))
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.composeActivity)
    implementation(Dependencies.composeNavigation)

    implementation(Dependencies.daggerHiltAndroid)
    kapt(Dependencies.daggerHiltAndroidCompiler)
    implementation(Dependencies.daggerHiltNavigation)

    implementation(Dependencies.roomRuntime)

    implementation(Dependencies.smbj)

    testImplementation(project(":core:unittest"))
    androidTestImplementation(project(":core:uitest"))

    androidTestUtil("androidx.test:orchestrator:1.4.2")
    androidTestImplementation(Dependencies.cucumberAndroid)
    androidTestImplementation(Dependencies.cucumberHilt)

    androidTestImplementation(Dependencies.daggerHiltAndroidTesting)
    kaptAndroidTest(Dependencies.daggerHiltAndroidCompiler)
}

afterEvaluate {
    tasks.getByName("generateCucumberReports") {
        dependsOn("downloadCucumberReports")
    }
}

cucumberReports {
    outputDir = file(buildDir.path + "/reports/cucumber/cucumber.html")
    buildId = "0"
    reports = files(buildDir.path + "/reports/cucumber/cucumber.json")
}

tasks.register("installTestApp") {
    dependsOn("assembleDebug", "assembleDebugAndroidTest")

    doLast {
        val adb = getAdbPath()
        val installCommand = "install"
        val debugApkPath = "$buildDir/outputs/apk/debug/app-debug.apk"
        val debugTestApkPath = "$buildDir/outputs/apk/androidTest/debug/app-debug-androidTest.apk"

        println("Installing debug app:")
        exec { commandLine(adb, installCommand, debugApkPath) }

        println("Installing test debug app:")
        exec { commandLine(adb, installCommand, debugTestApkPath) }
    }
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

        val devicePath = "/sdcard/cucumber.json"

        val adb = getAdbPath()

        exec {
            commandLine(
                adb,
                "shell",
                "su 0 cat ${getCucumberJsonDevicePath()} > $devicePath"
            )
        }
        exec { commandLine(adb, "pull", devicePath, localPath) }
    }
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

/**
 * The path which is used to store the Cucumber files.
 * @return
 */
fun getCucumberJsonDevicePath(): String {
    return "/data/user/0/com.msd.network.explorer/cache/reports/cucumber/cucumber.json"
}
