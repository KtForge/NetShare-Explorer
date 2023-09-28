plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
}

android {
    namespace = "com.msd.core.presentation"
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
}

dependencies {

    implementation(project(":core:navigation"))

    implementation(platform(Dependencies.kotlinBom))
    api(Dependencies.viewModelLifecycleKtx)
    implementation(Dependencies.inject)

    testImplementation(project(":core:unittest"))
}
