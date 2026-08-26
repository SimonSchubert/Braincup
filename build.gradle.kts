plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.spotless) apply false
}

tasks.register<Exec>("checkLocalizations") {
    group = "verification"
    description =
        "Checks that all composeResources string keys exist for every supported locale"
    commandLine(
        "python3",
        layout.projectDirectory.file("scripts/check_localizations.py").asFile.absolutePath,
    )
}

// Translating the app is only half of shipping a language: the Play listing needs its own supply
// folder, copy and screenshots, and none of that is produced by adding values-<locale>/strings.xml.
tasks.register<Exec>("checkStoreListings") {
    group = "verification"
    description =
        "Checks that every supported locale has a complete Play Store listing under fastlane/metadata/android"
    commandLine(
        "python3",
        layout.projectDirectory.file("scripts/check_store_listings.py").asFile.absolutePath,
        "--quiet",
    )
}
