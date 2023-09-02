import java.util.Properties

plugins {
    kotlin(Plugins.kapt)
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.daggerHiltAndroid)
}

val properties = Properties()
val propertiesFile = File("version")

var major = 0
var minor = 0
var patch = 0
var build = 0

if (propertiesFile.canRead()) {
    properties.load(propertiesFile.inputStream())

    major = (properties["version.major"] as String).toInt()
    minor = (properties["version.minor"] as String).toInt()
    patch = (properties["version.patch"] as String).toInt()
    build = (properties["version.build"] as String).toInt()
}

android {
    namespace = Configuration.namespace
    compileSdk = Configuration.compileSdk


    signingConfigs {
        create("release") {
            if (System.getenv("CI").toBoolean()) {
                storeFile = file(System.getenv()["CM_KEYSTORE_PATH"] as String)
                storePassword = System.getenv()["CM_KEYSTORE_PASSWORD"]
                keyAlias = System.getenv()["CM_KEY_ALIAS"]
                keyPassword = System.getenv()["CM_KEY_PASSWORD"]
            } else {
                val properties = Properties()
                val propertiesFile = File("key.properties")
                if (propertiesFile.canRead()) {
                    properties.load(propertiesFile.inputStream())

                    keyAlias = properties["key.alias"] as String
                    keyPassword = properties["key.alias.password"] as String
                    storeFile = file(properties["key.store.file"] as String)
                    storePassword = properties["key.store.password"] as String
                }
            }
        }
    }

    defaultConfig {
        applicationId = Configuration.namespace
        testApplicationId = Configuration.namespace + ".test"
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = major.times(10000) + minor.times(1000) + patch.times(100) + build
        versionName = "$major.$minor.$patch"

        // testInstrumentationRunner = Configuration.testInstrumentationRunner
        testInstrumentationRunner = "com.msd.network.explorer.test.HiltAndroidTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
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

    implementation(project(":data:smb_data"))
    implementation(project(":data:explorer_data"))

    implementation(project(":domain:smb"))
    implementation(project(":domain:explorer"))

    implementation(project(":feature:main"))
    implementation(project(":feature:edit"))
    implementation(project(":feature:explorer"))

    implementation(platform(Dependencies.kotlinBom))
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.composeActivity)
    implementation(Dependencies.composeNavigation)

    implementation(Dependencies.daggerHiltAndroid)
    kapt(Dependencies.daggerHiltAndroidCompiler)
    implementation(Dependencies.daggerHiltNavigation)

    implementation(Dependencies.roomRuntime)

    implementation(Dependencies.smbj)

    testImplementation(project(":core:unittest"))
    androidTestImplementation(project(":core:uitest"))
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.1")

    androidTestImplementation(Dependencies.cucumberAndroid)
    androidTestImplementation(Dependencies.cucumberHilt)

    androidTestImplementation(Dependencies.daggerHiltAndroidTesting)
    kaptAndroidTest(Dependencies.daggerHiltAndroidCompiler)
}
