plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(libs.junit)
    api(libs.mockito)
    api(libs.mockito.kotlin)
    api(libs.coroutines.test)

    implementation(libs.mockito.inline)
}
