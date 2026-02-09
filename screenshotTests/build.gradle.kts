plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.paparazzi)
    kotlin("android")
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "com.inspiredandroid.braincup.screenshots"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }

    sourceSets["main"].assets.srcDirs(
        "${project(":composeApp").projectDir}/build/generated/assets/copyDebugComposeResourcesToAndroidAssets",
    )
}

val preparePaparazzi by tasks.registering {
    dependsOn(":composeApp:copyDebugComposeResourcesToAndroidAssets")
}

tasks.matching { it.name.startsWith("testDebug") }.configureEach {
    dependsOn(preparePaparazzi)
}

tasks.withType<Test>().configureEach {
    reports.html.required.set(false)
}

val snapshotsDir = layout.projectDirectory.dir("src/test/snapshots/images")
val mediaDir = layout.projectDirectory.dir("../media")

tasks.register("updateScreenshots") {
    dependsOn("recordPaparazziDebug")

    val snapshotsDirFile = snapshotsDir.asFile
    val mediaDirFile = mediaDir.asFile

    doLast {
        val mapping = mapOf(
            "mainMenu" to "screen_android_01.png",
            "gameColorConfusion" to "screen_android_02.png",
            "gameAnomalyPuzzle" to "screen_android_03.png",
            "finishNewHighscore" to "screen_android_04.png",
            "gameMentalCalculation" to "screen_android_05.png",
            "gameSherlockCalculation" to "screen_android_06.png",
            "gameChainCalculation" to "screen_android_07.png",
            "gameFractionCalculation" to "screen_android_08.png",
            "gameValueComparison" to "screen_android_09.png",
            "gamePathFinder" to "screen_android_10.png",
            "gameGridSolver" to "screen_android_11.png",
            "gameVisualMemory" to "screen_android_12.png",
        )

        mapping.forEach { (testName, mediaName) ->
            val snapshot = snapshotsDirFile.listFiles()?.find { it.name.contains(testName) }
            if (snapshot != null) {
                snapshot.copyTo(mediaDirFile.resolve(mediaName), overwrite = true)
                println("Copied ${snapshot.name} -> media/$mediaName")
            } else {
                println("Warning: No snapshot found for $testName")
            }
        }
    }
}

dependencies {
    implementation(project(":composeApp"))
    testImplementation(compose.runtime)
    testImplementation(compose.material3)
    testImplementation(compose.foundation)
    testImplementation(compose.ui)
    testImplementation(compose.components.resources)
}
