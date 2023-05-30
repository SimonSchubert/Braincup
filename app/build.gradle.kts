plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = Config.targetSDK

    defaultConfig {
        applicationId = Config.applicationId
        minSdk = Config.minSDK
        targetSdk = Config.targetSDK
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner = Config.instrumentationRunner

        buildTypes {
            getByName("debug") {
                isShrinkResources = false
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            }
            getByName("release") {
                isShrinkResources = true
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            }
        }

        lint {
            checkReleaseBuilds = false
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        composeOptions {
            kotlinCompilerExtensionVersion = Lib.Versions.androidxUi
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }

    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources.excludes += listOf(
            "META-INF/*.kotlin_module",
            "DebugProbesKt.bin"
        )
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui-tooling:${Lib.Versions.androidxUi}")
    implementation("androidx.compose.ui:ui:${Lib.Versions.androidxUi}")
    implementation("androidx.compose.material:material:${Lib.Versions.androidxUi}")
    implementation("androidx.compose.material:material-ripple:${Lib.Versions.androidxUi}")
    implementation("androidx.activity:activity-compose:1.4.0")

    implementation("androidx.core:core-ktx:${Lib.Versions.androidxCoreKtx}")
    implementation("androidx.preference:preference-ktx:${Lib.Versions.androidxPreferenceKtx}")
    implementation("com.google.android.material:material:${Lib.Versions.materialDesign}")
    implementation("com.russhwolf:multiplatform-settings:${Lib.Versions.multiplatformSettings}")
}
