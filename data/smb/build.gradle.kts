plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
}

android {
    namespace = "com.msd.smb"
    compileSdk = Configuration.compileSdk

    defaultConfig {
        minSdk = Configuration.minSdk

        testInstrumentationRunner = Configuration.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {

    implementation(project(":domain:smb"))

    implementation(platform(Dependencies.kotlinBom))
    implementation(Dependencies.coreKtx)

    implementation(Dependencies.inject)

    implementation(Dependencies.roomRuntime)
    annotationProcessor(Dependencies.roomCompiler)
    kapt(Dependencies.roomCompiler)
    implementation(Dependencies.roomKtx)
}