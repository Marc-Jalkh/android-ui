enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("commonLibs") {
            from(files("./catalogs/common.versions.toml"))
        }
    }
}

rootProject.name = "kmm-project-startup"
include(":shared")