plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.daggerHiltAndroid)
}

android {
    namespace = Configuration.namespace
    compileSdk = Configuration.compileSdk

    defaultConfig {
        applicationId = Configuration.namespace
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = Configuration.versionCode
        versionName = Configuration.versionName

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