//Publishing script
//
//
//
//publishing {
//    repositories {
//        maven {
//            name = "kmmLibTest"
//        }
//    }
//    val thePublications = listOf(androidTarget) + "kotlinMultiplatform"
//    publications {
//        matching { it.name in thePublications }.all {
//            val targetPublication = this@all
//            tasks.withType<AbstractPublishToMaven>()
//                .matching { it.publication == targetPublication }
//                .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
//        }
//        matching { it.name.contains("ios", true) }.all {
//            val targetPublication = this@all
//            tasks.withType<AbstractPublishToMaven>()
//                .matching { it.publication == targetPublication }
//                .forEach { it.enabled = false }
//        }
//    }
//}
//
//afterEvaluate {
//    tasks.named("podPublishDebugXCFramework") {
//        enabled = false
//    }
//    tasks.named("podSpecDebug") {
//        enabled = false
//    }
//    tasks.withType<JavaCompile>().configureEach {
//        sourceCompatibility = JavaVersion.VERSION_17.toString()
//        targetCompatibility = JavaVersion.VERSION_17.toString()
//    }
//    tasks.withType<AbstractTestTask>().configureEach {
//        testLogging {
//            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//            events("started", "skipped", "passed", "failed")
//            showStandardStreams = true
//        }
//    }
//}
//
//val buildIdAttribute = Attribute.of("buildIdAttribute", String::class.java)
//configurations.forEach {
//    it.attributes {
//        attribute(buildIdAttribute, it.name)
//    }
//}
//
//val moveIosPodToRoot by tasks.registering {
//    group = commonLibs.versions.library.group.get()
//    doLast {
//        val releaseDir = rootProject.file(
//            "./release"
//        )
//        releaseDir.copyRecursively(
//            rootProject.file("./"),
//            true
//        )
//        releaseDir.deleteRecursively()
//    }
//}
//
//tasks.named("podPublishReleaseXCFramework") {
//    finalizedBy(moveIosPodToRoot)
//}
//
//val publishPlatforms by tasks.registering {
//    group = commonLibs.versions.library.group.get()
//    dependsOn(
////        tasks.named("publishAndroidReleasePublicationToGithubRepository"), //TODO !!
//        tasks.named("podPublishReleaseXCFramework")
//    )
//    doLast {
//        exec { commandLine = listOf("git", "add", "-A") }
//        exec { commandLine = listOf("git", "commit", "-m", "iOS binary lib for version ${commonLibs.versions.library.version.get()}") }
//        exec { commandLine = listOf("git", "push", "origin", "master") }
//        exec { commandLine = listOf("git", "tag", commonLibs.versions.library.version.get()) }
//        exec { commandLine = listOf("git", "push", "--tags") }
//        println("=============version ${commonLibs.versions.library.version.get()} built and published")
//    }
//}
//
//val compilePlatforms by tasks.registering {
//    group = commonLibs.versions.library.group.get()
//    dependsOn(
//        tasks.named("compileKotlinIosArm64"),
//        tasks.named("compileKotlinIosX64"),
//        tasks.named("compileKotlinIosSimulatorArm64"),
//        tasks.named("compileReleaseKotlinAndroid")
//    )
//    doLast {
//        println("Finished compilation")
//    }
//}

