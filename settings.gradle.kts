
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
include(":core:ui")
include(":core:presentation")
include(":core:navigation")
include(":domain:smb")
include(":feature:main")
include(":data:smb_data")
include(":feature:edit")
include(":feature:explorer")
include(":domain:explorer")
include(":data:explorer_data")
include(":core:unittest")
include(":core:uitest")
include(":cucumber")
include(":core:tracking")
include(":data:files")
