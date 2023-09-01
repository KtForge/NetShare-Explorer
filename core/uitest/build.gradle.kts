plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
}

android {
    namespace = "com.msd.core.uitest"
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
}

dependencies {

    api(Dependencies.cucumberAndroid)
    api(Dependencies.cucumberHilt)
    api(Dependencies.cucumberJava)
    api(Dependencies.cucumberJUnit)

    api(Dependencies.androidxTestRunner)

    api(Dependencies.espressoCore)
    api(Dependencies.mockitoAndroid)
    api(Dependencies.mockitoKotlin)
    api(Dependencies.uiTestJUnit4)
    debugApi(Dependencies.uiTestManifest)
}
