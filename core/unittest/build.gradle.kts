plugins {
    id(Plugins.javaLibrary)
    id(Plugins.kotlinJvm)
}

java {
    sourceCompatibility = Configuration.javaVersion
    targetCompatibility = Configuration.javaVersion
}

dependencies {
    api(Dependencies.jUnit)
    api(Dependencies.mockitoKotlin)
    api(Dependencies.coroutinesTest)

    testImplementation(Dependencies.mockitoInline)
}
