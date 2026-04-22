plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.inspiredandroid.braincup.app"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.inspiredandroid.braincup"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode =
            libs.versions.android.versionCode
                .get()
                .toInt()
        versionName = libs.versions.appVersion.get()
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("playStore") {
            dimension = "distribution"
        }
        create("foss") {
            dimension = "distribution"
            isDefault = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            val envKeystore = System.getenv("KEYSTORE_FILE")
            val keystoreFile =
                if (envKeystore != null) {
                    file(envKeystore)
                } else {
                    rootProject.layout.projectDirectory
                        .file("keystore.jks")
                        .asFile
                }
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            val hasReleaseKeystore =
                System.getenv("KEYSTORE_FILE") != null ||
                    rootProject.layout.projectDirectory
                        .file("keystore.jks")
                        .asFile
                        .exists()
            signingConfig =
                if (hasReleaseKeystore) {
                    signingConfigs.getByName("release")
                } else {
                    signingConfigs.getByName("debug")
                }
        }
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    "playStoreImplementation"(libs.play.review)
}
