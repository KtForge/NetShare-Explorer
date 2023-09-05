plugins {
    kotlin(Plugins.kapt)
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.msd.network.explorer.test"
    compileSdk = Configuration.compileSdk

    defaultConfig {
        minSdk = Configuration.minSdk
        testApplicationId = "com.msd.network.explorer.test"

        testInstrumentationRunner = "com.msd.network.explorer.test.ExplorerCucumberTestRunner"
    }
    compileOptions {
        sourceCompatibility = Configuration.javaVersion
        targetCompatibility = Configuration.javaVersion
    }

    targetProjectPath = ":app"
}

dependencies {

    implementation(project(":core:uitest"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:presentation"))

    implementation(project(":data:smb_data"))
    implementation(project(":data:explorer_data"))

    implementation(project(":domain:smb"))
    implementation(project(":domain:explorer"))

    implementation(project(":feature:main"))
    implementation(project(":feature:edit"))
    implementation(project(":feature:explorer"))

    implementation(Dependencies.roomRuntime)

    implementation(Dependencies.smbj)

    implementation(Dependencies.cucumberAndroid)
    implementation(Dependencies.cucumberHilt)

    implementation(Dependencies.daggerHiltAndroid)
    implementation(Dependencies.daggerHiltAndroidTesting)
    kapt(Dependencies.daggerHiltAndroidCompiler)
}