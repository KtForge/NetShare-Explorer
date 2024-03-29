plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.msd.feature.explorer"
    compileSdk = 34


    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
}

dependencies {

    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:presentation"))

    implementation(project(":domain:smb"))
    implementation(project(":domain:explorer"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.core.ktx)
    implementation(libs.compose.activity)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    testImplementation(project(":core:unittest"))
    androidTestImplementation(project(":core:uitest"))
}
