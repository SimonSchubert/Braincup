plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.paparazzi)
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

    sourceSets {
        getByName("main") {
            assets.srcDirs(
                "${project(":composeApp").projectDir}/build/generated/compose/resourceGenerator/preparedResources/commonMain",
            )
        }
    }
}

val preparePaparazzi by tasks.registering {
    dependsOn(":composeApp:prepareComposeResourcesTaskForCommonMain")
    dependsOn(":composeApp:copyNonXmlValueResourcesForCommonMain")
    dependsOn(":composeApp:convertXmlValueResourcesForCommonMain")
}

tasks
    .matching {
        it.name.startsWith("testDebug") ||
            (it.name.startsWith("merge") && it.name.endsWith("Assets"))
    }.configureEach {
        dependsOn(preparePaparazzi)
    }

tasks.withType<Test>().configureEach {
    reports.html.required.set(false)
    // Recycle the JVM periodically so Paparazzi's native ImageReader buffers don't
    // exhaust when running the full Store/Tablet locale matrix in one fork.
    forkEvery = 50
    maxHeapSize = "4g"
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
            "gameColoredShapes" to "screen_android_02.png",
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
            val snapshot = snapshotsDirFile.listFiles()?.find {
                it.name.endsWith("_ScreenshotTest_$testName.png")
            }
            if (snapshot != null) {
                snapshot.copyTo(mediaDirFile.resolve(mediaName), overwrite = true)
                println("Copied ${snapshot.name} -> media/$mediaName")
            } else {
                println("Warning: No snapshot found for $testName")
            }
        }
    }
}

val fastlaneDir: Directory? = layout.projectDirectory.dir("../fastlane/metadata/android")

tasks.matching { it.name == "testDebugUnitTest" }.configureEach {
    val task = this as Test
    if (gradle.startParameter.taskNames.any { it.contains("generateStoreScreenshots") }) {
        task.filter.includeTestsMatching("*.StoreScreenshotTest")
        task.filter.includeTestsMatching("*.TabletStoreScreenshotTest")
    }
}

tasks.register("generateStoreScreenshots") {
    dependsOn("recordPaparazziDebug")

    val snapshotsDirFile = snapshotsDir.asFile
    val fastlaneDirFile = fastlaneDir?.asFile

    doLast {
        val phoneRegex = Regex("""StoreScreenshotTest_\w+\[([^\]]+)\]_store_[a-z-]+_(\d+(?:_\w+)?)\.png""")
        val tabletRegex = Regex("""TabletStoreScreenshotTest_\w+\[([^\]]+)\]_tablet_[a-z-]+_(\d+(?:_\w+)?)\.png""")

        val allPngs = snapshotsDirFile.listFiles()?.filter { it.extension == "png" } ?: emptyList()

        val phoneSnapshots = allPngs.filter {
            it.name.contains("StoreScreenshotTest_") &&
                !it.name.contains("TabletStoreScreenshotTest_") &&
                it.name.contains("_store_")
        }
        val tabletSnapshots = allPngs.filter {
            it.name.contains("TabletStoreScreenshotTest_") && it.name.contains("_tablet_")
        }

        if (phoneSnapshots.isEmpty() && tabletSnapshots.isEmpty()) {
            println("No store screenshots found.")
            return@doLast
        }

        phoneSnapshots.forEach { file ->
            val match = phoneRegex.find(file.name) ?: return@forEach
            val (locale, name) = match.destructured
            val targetDir = File(fastlaneDirFile, "$locale/images/phoneScreenshots")
            targetDir.mkdirs()
            val targetFile = File(targetDir, "$name.png")
            file.copyTo(targetFile, overwrite = true)
            println("Copied -> $locale/phoneScreenshots/$name.png")
        }

        tabletSnapshots.forEach { file ->
            val match = tabletRegex.find(file.name) ?: return@forEach
            val (locale, name) = match.destructured
            val targetDir = File(fastlaneDirFile, "$locale/images/tenInchScreenshots")
            targetDir.mkdirs()
            val targetFile = File(targetDir, "$name.png")
            file.copyTo(targetFile, overwrite = true)
            println("Copied -> $locale/tenInchScreenshots/$name.png")
        }
    }
}

dependencies {
    implementation(project(":composeApp"))
    testImplementation(libs.compose.runtime)
    testImplementation(libs.compose.material3)
    testImplementation(libs.compose.foundation)
    testImplementation(libs.compose.ui)
    testImplementation(libs.compose.components.resources)
}
