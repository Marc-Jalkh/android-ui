import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("kover")
    id("com.android.library")
    id("kotlinx-serialization")
}

group = commonLibs.versions.library.group.get()
version = commonLibs.versions.library.version.get()
var androidTarget: String = ""

kotlin {
    android {
        publishLibraryVariants("release")
        publishLibraryVariantsGroupedByFlavor = true
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += listOf("-Xgc=cms")
        }
    }


    cocoapods {
        val iosDefinitions = commonLibs.versions.ios
        name = iosDefinitions.baseName.get()
        summary = iosDefinitions.summary.get()
        homepage = iosDefinitions.homepage.get()
        authors = iosDefinitions.authors.get()
        version = commonLibs.versions.library.version.get()
        ios.deploymentTarget = iosDefinitions.deployment.target.get()
        framework {
            baseName = "shared"
            isStatic = true
            transitiveExport = true
            embedBitcode(org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode.BITCODE)
        }
        pod(name = "libPhoneNumber-iOS") {
            moduleName = "libPhoneNumber_iOS"
            source = git("https://github.com/iziz/libPhoneNumber-iOS")
        }
        publishDir = rootProject.file("./")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(commonLibs.kotlin.stdlib)

                with(commonLibs.kotlin){
                    implementation(dateTime)
                    implementation(coroutinesCore)
                    implementation(serializationJson)
                }

                with(commonLibs.ktor){
                    implementation(clientCore)
                    implementation(clientJson)
                    implementation(json)
                    implementation(clientLogging)
                    implementation(auth)
                    implementation(contentNegotiation)
                }

            }
        }

        val androidMain by getting {
            dependencies {
                implementation("com.googlecode.libphonenumber:libphonenumber:8.12.32")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(commonLibs.ktor.clientDarwin)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

publishing {
    repositories {
        maven {
            setUrl("https://github.com/Marc-Jalkh/android-ui")
        }
    }
}

android {
    namespace = commonLibs.versions.library.group.get()
    compileSdk = commonLibs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = commonLibs.versions.android.minSdk.get().toInt()
        targetSdk = commonLibs.versions.android.compileSdk.get().toInt()
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    beforeEvaluate {
        libraryVariants.all {
            compileOptions {
                // Flag to enable support for the new language APIs
                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}
