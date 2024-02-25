pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.16.2"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceAgree = "yes"
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
    }
}

rootProject.name = "NetShare Explorer"

include(":app")
include(":core:navigation", ":core:presentation", ":core:tracking", ":core:ui", ":core:uitest", ":core:unittest")
include(":data:explorer_data", ":data:files", ":data:smb_data")
include(":domain:explorer", ":domain:smb")
include(":feature:edit", ":feature:explorer", ":feature:main")
include(":cucumber")
