plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id("jacoco-reports")
}

android {
    namespace = "com.msd.feature.main"
    compileSdk = Configuration.compileSdk


    defaultConfig {
        minSdk = Configuration.minSdk

        testInstrumentationRunner = Configuration.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
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
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:presentation"))
    implementation(project(":core:tracking"))

    implementation(project(":domain:smb"))

    implementation(platform(Dependencies.kotlinBom))
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.composeActivity)

    implementation(Dependencies.daggerHiltAndroid)
    kapt(Dependencies.daggerHiltAndroidCompiler)
    kapt(Dependencies.daggerHiltAndroidCompiler)

    testImplementation(project(":core:unittest"))
    androidTestImplementation(project(":core:uitest"))
}
