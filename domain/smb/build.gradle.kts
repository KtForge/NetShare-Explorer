plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    jacoco
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(libs.coroutines.core)
    implementation(libs.inject)

    testImplementation(project(":core:unittest"))
}
