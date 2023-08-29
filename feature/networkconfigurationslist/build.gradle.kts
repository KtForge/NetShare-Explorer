plugins {
    jacoco
    kotlin(Plugins.kapt)
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
}

jacoco {
    toolVersion = Versions.jacocoVersion
    reportsDirectory.set(file("$buildDir/reports/coverage"))
}

tasks.withType<Test> {
    jacoco.ext.set("includeNoLocationClasses", true)
    jacoco.ext.set("excludes", "jdk.internal.*")
}

tasks.create<JacocoReport>("jacocoCombinedTestReports") {
    dependsOn(setOf("testDebugUnitTest", "createDebugCoverageReport"))
    group = "Verification"
    description = "Creates JaCoCo test coverage report for Unit and Instrumented Tests (combined) on the Debug build"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val excludes = setOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    classDirectories.setFrom(fileTree(
        "dir" to "$buildDir/intermediates/classes/debug",
        excludes = excludes
    ) + fileTree(
    dir: "$buildDir/tmp/kotlin-classes/debug",
    excludes: excludes
    )
    )
}

    // generated classes
    classDirectories.from = fileTree(
        dir: "$buildDir/intermediates/classes/debug",
    excludes: excludes
    ) + fileTree(
    dir: "$buildDir/tmp/kotlin-classes/debug",
    excludes: excludes
    )
    // sources
    sourceDirectories.from = [
        android.sourceSets.main.java.srcDirs,
        "src/main/kotlin"
    ]
    // Output and existing data
    // Combine Unit test and Instrumented test reports
    executionData.from = fileTree(dir: "$buildDir", includes: [
    // Unit tests coverage data
    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
    // Instrumented tests coverage data
    "outputs/code_coverage/debugAndroidTest/connected/*coverage.ec"
    ])
}

android {
    namespace = "com.msd.networkconfigurationslist"
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

    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:presentation"))

    implementation(project(":domain:smb"))

    implementation(platform(Dependencies.kotlinBom))
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.composeActivity)

    implementation(Dependencies.daggerHiltAndroid)
    kapt(Dependencies.daggerHiltAndroidCompiler)
    kapt(Dependencies.daggerHiltAndroidCompiler)

    testImplementation(project(":core:unittest"))
}
