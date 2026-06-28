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
