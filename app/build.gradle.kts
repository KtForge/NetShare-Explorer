import java.util.Properties

plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.daggerHiltAndroid)
}

val properties = Properties()
val propertiesFile = File("version")
properties.load(propertiesFile.inputStream())

val major = (properties["version.major"] as String).toInt()
val minor = (properties["version.minor"] as String).toInt()
val patch = (properties["version.patch"] as String).toInt()
val build = (properties["version.build"] as String).toInt()

android {
    namespace = Configuration.namespace
    compileSdk = Configuration.compileSdk

    defaultConfig {
        applicationId = Configuration.namespace
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = major.times(10000) + minor.times(1000) + patch.times(100) + build
        versionName = "$major.$minor.$patch"

        testInstrumentationRunner = Configuration.testInstrumentationRunner
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation(project(":data:smb"))
    implementation(project(":data:explorer"))

    implementation(project(":domain:smb"))
    implementation(project(":domain:explorer"))

    implementation(project(":feature:networkconfigurationslist"))
    implementation(project(":feature:editnetworkconfiguration"))
    implementation(project(":feature:explorer"))

    implementation(platform(Dependencies.kotlinBom))
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.composeActivity)
    implementation(Dependencies.composeNavigation)

    implementation(Dependencies.daggerHiltAndroid)
    kapt(Dependencies.daggerHiltAndroidCompiler)
    implementation(Dependencies.daggerHiltNavigation)

    implementation(Dependencies.roomRuntime)
}