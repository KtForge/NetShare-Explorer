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
include(":feature:networkconfigurationslist")
include(":data:smb")
include(":feature:editnetworkconfiguration")
include(":feature:explorer")
include(":domain:explorer")
include(":data:explorer")
