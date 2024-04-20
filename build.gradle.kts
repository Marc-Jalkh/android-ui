plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(commonLibs.plugins.android.lib).apply(false)
    alias(commonLibs.plugins.multiplatform).apply(false)
    alias(commonLibs.plugins.kotlin.cocoapods).apply(false)
    alias(commonLibs.plugins.android.app).apply(false)
    alias(commonLibs.plugins.kover).apply(false)
    id("maven-publish")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.21")
    }
}