plugins {
    id(Plugins.javaLibrary)
    id(Plugins.kotlinJvm)
}

java {
    sourceCompatibility = Configuration.javaVersion
    targetCompatibility = Configuration.javaVersion
}

dependencies {

    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.inject)

    testImplementation(project(":core:unittest"))
}
