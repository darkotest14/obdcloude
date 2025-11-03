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

rootProject.name = "OBDCloud"

// App module
include(":app")

// Core modules
include(":core:ui")
include(":core:design-system")
include(":core:data")
include(":core:domain")
include(":core:database")
include(":core:network")
include(":core:testing")

// Feature modules
include(":feature:adapter")
include(":feature:diagnostics")
include(":feature:vehicle")
include(":feature:coding")
include(":feature:service")

include(":test-app")