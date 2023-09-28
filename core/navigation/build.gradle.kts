plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
}

android {
    namespace = "com.msd.core.navigation"
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
