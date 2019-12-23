plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("android.extensions")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Config.targetSDK)

    defaultConfig {
        applicationId = Config.applicationId
        minSdkVersion(Config.minSDK)
        targetSdkVersion(Config.targetSDK)
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

        lintOptions {
            isCheckReleaseBuilds = false
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.compose:compose-runtime:${Lib.Versions.androidxUi}")
    implementation("androidx.ui:ui-framework:${Lib.Versions.androidxUi}")
    implementation("androidx.ui:ui-layout:${Lib.Versions.androidxUi}")
    implementation("androidx.ui:ui-material:${Lib.Versions.androidxUi}")
    implementation("androidx.ui:ui-foundation:${Lib.Versions.androidxUi}")
    implementation("androidx.ui:ui-animation:${Lib.Versions.androidxUi}")
    implementation("androidx.ui:ui-tooling:${Lib.Versions.androidxUi}")

    implementation("androidx.core:core-ktx:${Lib.Versions.androidxCoreKtx}")
    implementation("androidx.preference:preference-ktx:${Lib.Versions.androidxPreferenceKtx}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Lib.Versions.kotlin}")
    implementation("com.google.android.material:material:${Lib.Versions.materialDesign}")
    implementation("com.russhwolf:multiplatform-settings:${Lib.Versions.multiplatformSettings}")
}
