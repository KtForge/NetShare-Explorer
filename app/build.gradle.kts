import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.ksp)
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
    namespace = "com.msd.network.explorer"
    compileSdk = 34

    signingConfigs {
        create("release") {
            if (System.getenv("CI").toBoolean()) {
                storeFile = file(projectDir.absolutePath + "/nfe_android_keystore")
                storePassword = System.getenv()["ANDROID_KEYSTORE_PASSWORD"]
                keyAlias = System.getenv()["ANDROID_KEYSTORE_ALIAS"]
                keyPassword = System.getenv()["ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD"]
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
        applicationId = "com.msd.network.explorer"
        testApplicationId = "com.msd.network.explorer.test"
        minSdk = 26
        targetSdk = 34
        versionCode = major.times(1000000) + minor.times(10000) + patch.times(100) + build
        versionName = "$major.$minor.$patch"

        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
}

dependencies {

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

    implementation(platform(libs.kotlin.bom))
    implementation(libs.core.ktx)
    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt.navigation)

    implementation(libs.room.runtime)

    implementation(libs.smbj)
}
