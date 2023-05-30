// import org.jetbrains.compose.compose

repositories {
    google()
}

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "com.inspiredandroid.braincup"
version = "1.0"

kotlin {
    android()
    jvm("cli") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
            }
        }
        val cliMain by getting {
            resources.setSrcDirs(listOf("../assets", "../cli/src/jvmMain/resources"))
        }
        val cliTest by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/appMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 22
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
    }

    namespace = "com.inspiredandroid.braincup"
}