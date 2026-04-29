import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.spotless)
}

kotlin {
    applyDefaultHierarchyTemplate()
    android {
        namespace = "com.inspiredandroid.braincup"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = rootProject.layout.projectDirectory.asFile.path
            val projectDirPath = layout.projectDirectory.asFile.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static(rootDirPath)
                        static(projectDirPath)
                    }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting
        val nonIosMain by creating { dependsOn(commonMain.get()) }
        androidMain.get().dependsOn(nonIosMain)
        desktopMain.dependsOn(nonIosMain)
        wasmJsMain.get().dependsOn(nonIosMain)

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.navigation.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.inspiredandroid.braincup.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
            packageName = "Braincup"
            packageVersion = libs.versions.appVersion.get()

            macOS {
                iconFile.set(project.file("icon.icns"))
            }
            windows {
                iconFile.set(project.file("icon.ico"))
            }
            linux {
                iconFile.set(project.file("icon.png"))
                modules("jdk.security.auth")
            }
        }
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("1.5.0").editorConfigOverride(
            mapOf(
                "ktlint_standard_no-wildcard-imports" to "disabled",
                "ktlint_standard_function-naming" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
    }
}

tasks.register("updateIosVersion") {
    description = "Updates iOS version in Config.xcconfig from libs.versions.toml"
    group = "build"

    val version = libs.versions.appVersion.get()
    val configFile =
        rootProject.layout.projectDirectory
            .file("iosApp/Configuration/Config.xcconfig")
            .asFile

    inputs.property("version", version)
    outputs.file(configFile)

    doLast {
        if (configFile.exists()) {
            val content = configFile.readText()
            val updatedContent =
                content.replace(
                    Regex("MARKETING_VERSION=.*"),
                    "MARKETING_VERSION=$version",
                )
            configFile.writeText(updatedContent)
            println("Updated iOS MARKETING_VERSION to $version")
        }
    }
}

tasks.matching { it.name.startsWith("compileKotlinIos") }.configureEach {
    dependsOn("updateIosVersion")
}
