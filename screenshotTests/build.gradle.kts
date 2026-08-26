import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

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
            // Generated compose multiplatform resources from :composeApp (prepared for Paparazzi).
            assets.directories.add(
                rootProject.layout.projectDirectory
                    .dir("composeApp/build/generated/compose/resourceGenerator/preparedResources/commonMain")
                    .asFile
                    .path,
            )
        }
    }
}

val preparePaparazzi =
    tasks.register("preparePaparazzi") {
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

val renderDeviceFrames =
    tasks.register<Exec>("renderDeviceFrames") {
        description = "Wraps the DesktopFrameScreenshotTest snapshots in macOS/browser window frames."
        dependsOn("recordPaparazziDebug")
        workingDir = layout.projectDirectory.dir("..").asFile
        commandLine("python3", "scripts/render_device_frames.py")
    }

tasks.register("updateDesktopScreenshots") {
    description = "Records only the desktop-frame snapshots and rebuilds media/screen_mac_*.png and media/screen_web_*.png."
    dependsOn(renderDeviceFrames)
}

val fastlaneDir: Directory? = layout.projectDirectory.dir("../fastlane/metadata/android")

tasks.matching { it.name == "testDebugUnitTest" }.configureEach {
    val task = this as Test
    val requestedTasks = gradle.startParameter.taskNames
    if (requestedTasks.any {
            it.contains("generateStoreScreenshots") ||
                it.contains("updateEnUsStoreScreenshots") ||
                it.contains("updateNewLocaleStoreScreenshots")
        }
    ) {
        task.filter.includeTestsMatching("*.StoreScreenshotTest")
        task.filter.includeTestsMatching("*.TabletStoreScreenshotTest")
    }
    if (requestedTasks.any { it.contains("generateIosStoreScreenshots") }) {
        task.filter.includeTestsMatching("*.IosStoreScreenshotTest")
        task.filter.includeTestsMatching("*.IosTabletStoreScreenshotTest")
    }
    if (requestedTasks.any { it.contains("updateDesktopScreenshots") }) {
        task.filter.includeTestsMatching("*.DesktopFrameScreenshotTest")
    }
}

// Lays the recorded Play snapshots out in fastlane's supply tree. `locales` limits the copy to a
// subset of the rendered matrix; null copies every locale. `onlyMissing` further narrows it to
// locales whose supply folder holds no images yet, which is how a newly added language picks up
// its store screenshots without reshuffling the other 45.
fun registerStoreScreenshotCopy(
    name: String,
    locales: Set<String>?,
    taskDescription: String,
    onlyMissing: Boolean = false,
) = tasks.register(name) {
    description = taskDescription
    dependsOn("recordPaparazziDebug")

    val snapshotsDirFile = snapshotsDir.asFile
    val fastlaneDirFile = fastlaneDir?.asFile

    doLast {
        val phoneRegex = Regex("""StoreScreenshotTest_\w+\[([^\]]+)\]_store_[a-zA-Z0-9-]+_(\d+(?:_\w+)?)\.png""")
        val tabletRegex = Regex("""TabletStoreScreenshotTest_\w+\[([^\]]+)\]_tablet_[a-zA-Z0-9-]+_(\d+(?:_\w+)?)\.png""")

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

        // Snapshot files carry the test method name, so renaming a store test leaves the old file
        // behind claiming the same slot. Both would map to "<slot>.png" and whichever the directory
        // listing yielded last would win, silently shipping the wrong image. Fail instead.
        fun assertOneFilePerSlot(files: List<File>, regex: Regex, kind: String) {
            files.groupBy { file ->
                val match = regex.find(file.name) ?: return@groupBy null
                val (locale, screen) = match.destructured
                locale to screen
            }.forEach { (slot, claimants) ->
                if (slot != null && claimants.size > 1) {
                    throw GradleException(
                        "$kind slot ${slot.first}/${slot.second} is claimed by ${claimants.size} snapshots: " +
                            claimants.joinToString { it.name } +
                            ". A store test was probably renamed; delete the stale snapshot and re-record.",
                    )
                }
            }
        }

        assertOneFilePerSlot(phoneSnapshots, phoneRegex, "phone")
        assertOneFilePerSlot(tabletSnapshots, tabletRegex, "tablet")

        // Sampled once, before the first copy: checking the directory inside the loop would see
        // the image this task just wrote and skip every remaining slot of the same locale.
        fun hasImages(dir: File) = dir.listFiles()?.any { it.extension == "png" } == true
        val alreadyPopulated = fastlaneDirFile
            ?.listFiles()
            ?.filter { it.isDirectory }
            ?.filter { hasImages(File(it, "images/phoneScreenshots")) || hasImages(File(it, "images/tenInchScreenshots")) }
            ?.map { it.name }
            ?.toSet()
            ?: emptySet()

        phoneSnapshots.forEach { file ->
            val match = phoneRegex.find(file.name) ?: return@forEach
            val (locale, screen) = match.destructured
            if (locales != null && locale !in locales) return@forEach
            if (onlyMissing && locale in alreadyPopulated) return@forEach
            val targetDir = File(fastlaneDirFile, "$locale/images/phoneScreenshots")
            targetDir.mkdirs()
            val targetFile = File(targetDir, "$screen.png")
            file.copyTo(targetFile, overwrite = true)
            println("Copied -> $locale/phoneScreenshots/$screen.png")
        }

        tabletSnapshots.forEach { file ->
            val match = tabletRegex.find(file.name) ?: return@forEach
            val (locale, screen) = match.destructured
            if (locales != null && locale !in locales) return@forEach
            if (onlyMissing && locale in alreadyPopulated) return@forEach
            val targetDir = File(fastlaneDirFile, "$locale/images/tenInchScreenshots")
            targetDir.mkdirs()
            val targetFile = File(targetDir, "$screen.png")
            file.copyTo(targetFile, overwrite = true)
            println("Copied -> $locale/tenInchScreenshots/$screen.png")
        }
    }
}

registerStoreScreenshotCopy(
    name = "generateStoreScreenshots",
    locales = null,
    taskDescription = "Lays every locale's Play store snapshots out for 'fastlane android upload_screenshots'.",
)

// The README renders the en-US store images straight from the supply tree, so CI refreshes just
// that locale on every push; the full matrix stays a manual step before a store release.
val updateEnUsStoreScreenshots =
    registerStoreScreenshotCopy(
        name = "updateEnUsStoreScreenshots",
        locales = setOf("en-US"),
        taskDescription = "Refreshes only the en-US Play store images, which the README links to.",
    )

// recordPaparazziDebug renders all 46 locales on every run regardless, so filling in a locale that
// has no supply images yet is free. It keeps a language added since the last full refresh from
// shipping with an English store page, without committing 552 PNGs on every push.
val updateNewLocaleStoreScreenshots =
    registerStoreScreenshotCopy(
        name = "updateNewLocaleStoreScreenshots",
        locales = null,
        taskDescription = "Copies store images only for locales whose supply folder is still empty.",
        onlyMissing = true,
    )

tasks.register("updateScreenshots") {
    description =
        "Rebuilds the images the README links to: the desktop/web frames, the en-US store set " +
        "and the store images of any locale that has none yet."
    dependsOn(renderDeviceFrames, updateEnUsStoreScreenshots, updateNewLocaleStoreScreenshots)
}

val appStoreScreenshotsDir: Directory? = layout.projectDirectory.dir("../fastlane/screenshots")

tasks.register("generateIosStoreScreenshots") {
    description = "Records the iPhone 6.9\" and iPad 13\" snapshots and lays them out for 'fastlane ios upload_screenshots'."
    dependsOn("recordPaparazziDebug")

    val snapshotsDirFile = snapshotsDir.asFile
    val appStoreDirFile = appStoreScreenshotsDir?.asFile

    doLast {
        // App Store Connect rejects screenshots that carry an alpha channel, and Paparazzi writes
        // RGBA, so every snapshot is flattened on the way out instead of plain-copied.
        fun writeOpaque(source: File, target: File) {
            val snapshot = ImageIO.read(source)
            val opaque = BufferedImage(snapshot.width, snapshot.height, BufferedImage.TYPE_INT_RGB)
            val graphics = opaque.createGraphics()
            graphics.drawImage(snapshot, 0, 0, Color.BLACK, null)
            graphics.dispose()
            ImageIO.write(opaque, "png", target)
        }

        val sets = listOf(
            Regex("""IosStoreScreenshotTest_\w+\[([^\]]+)\]_iphone_[a-z-]+_(\d+)\.png""") to "iphone69",
            Regex("""IosTabletStoreScreenshotTest_\w+\[([^\]]+)\]_ipad_[a-z-]+_(\d+)\.png""") to "ipad13",
        )

        val allPngs = snapshotsDirFile.listFiles()?.filter { it.extension == "png" } ?: emptyList()
        var copied = 0

        sets.forEach { (regex, prefix) ->
            allPngs.forEach { file ->
                val match = regex.find(file.name) ?: return@forEach
                val (locale, name) = match.destructured
                val targetDir = File(appStoreDirFile, locale)
                targetDir.mkdirs()
                writeOpaque(file, File(targetDir, "${prefix}_$name.png"))
                println("Copied -> $locale/${prefix}_$name.png")
                copied++
            }
        }

        if (copied == 0) {
            println("No App Store screenshots found.")
        }
    }
}

dependencies {
    implementation(projects.composeApp)
    testImplementation(libs.compose.runtime)
    testImplementation(libs.compose.material3)
    testImplementation(libs.compose.foundation)
    testImplementation(libs.compose.ui)
    testImplementation(libs.compose.components.resources)
    testImplementation(libs.kotlinx.collections.immutable)
}
