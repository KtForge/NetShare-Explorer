plugins {
    id(Plugins.javaLibrary)
    id(Plugins.kotlinJvm)
    jacoco
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

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
    dependsOn(tasks.test)
}
