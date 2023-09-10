pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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
