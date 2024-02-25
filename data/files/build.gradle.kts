plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.msd.data.files"
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
}

dependencies {

    implementation(project(":domain:explorer"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.core.ktx)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    androidTestImplementation(project(":core:uitest"))
}
