import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Properties

plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.daggerHiltAndroid)
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
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
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

tasks.create("downloadCucumberReports") {
    group = "Verification"
    description =
        "Downloads the rich Cucumber report files (HTML, XML, JSON) from the connected device"

    doLast {
        val deviceSourcePath = getCucumberDevicePath()
        println("Device source path: $deviceSourcePath")
        val localReportPath = File(buildDir, "reports/cucumber")
        println("local report path: $localReportPath")
        if (!localReportPath.exists()) {
            localReportPath.mkdirs()
        }
        if (!localReportPath.exists()) {
            throw GradleException("Could not create $localReportPath")
        }
        val adb = getAdbPath()
        val files = getCucumberReportFileNames()
        files.forEach { fileName ->
            println(fileName)
            exec {
                commandLine(adb, "pull", "$deviceSourcePath/$fileName", localReportPath)
            }
        }
    }
}

/**
 * Deletes existing Cucumber reports on the device.
 */
tasks.create("deleteExistingCucumberReports") {
    group = "Verification"
    description =
        "Removes the rich Cucumber report files (HTML, XML, JSON) from the connected device"
    doLast {
        val deviceSourcePath = getCucumberDevicePath()
        val files = getCucumberReportFileNames()
        files.forEach { fileName ->
            val deviceFileName = "$deviceSourcePath/$fileName"
            val output2 =
                executeAdb("if [ -d $deviceFileName ]; then rm -r $deviceFileName; else rm -r $deviceFileName ; fi")
            println(output2)
        }
    }
}

/**
 * Sets the required permissions for Cucumber to write on the internal storage.
 */
tasks.create("grantPermissions") {
    dependsOn("installDebug")

    doLast {
        val adb = getAdbPath()
        // We only set the permissions for the main application
        val mainPackageName = "com.msd.network.explorer"
        val readPermission = "android.permission.READ_EXTERNAL_STORAGE"
        val writePermission = "android.permission.WRITE_EXTERNAL_STORAGE"
        exec { commandLine(adb, "shell", "pm", "grant", mainPackageName, readPermission) }
        exec { commandLine(adb, "shell", "pm", "grant", mainPackageName, writePermission) }
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
 * Sometime adb returns '\r' character multiple times.
 * @param s the original string returned by adb
 * @return the fixed string without '\r'
 */
fun fixAdbOutput(s: String): String {
    return s.replace("[\r\n]+", "\n").trim()
}

/**
 * Runs the adb tool
 * @param program the program which is executed on the connected device
 * @return the output of the adb tool
 */
fun executeAdb(program: String): String {
    val process = ProcessBuilder(getAdbPath(), "shell", program).redirectErrorStream(true).start()
    val text = BufferedReader(InputStreamReader(process.inputStream)).toString()
    return fixAdbOutput(text)
}

/**
 * The path which is used to store the Cucumber files.
 * @return
 */
fun getCucumberDevicePath(): String {
    return "/storage/emulated/0/Documents/reports"
}

/**
 * @return the known Cucumber report files/directories
 */
fun getCucumberReportFileNames(): Array<String> {
    return arrayOf("cucumber.xml", "cucumber.html")
}