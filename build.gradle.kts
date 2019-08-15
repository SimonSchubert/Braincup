import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    val kotlin_version = "1.3.41"

    repositories {
        google()
        maven ("https://plugins.gradle.org/m2/")
        maven ("https://dl.bintray.com/kotlin/kotlin-eap" )
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("gradle.plugin.com.wiredforcode:gradle-spawn-plugin:0.8.2")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.22.0"
}

allprojects {
    repositories {
        maven ( "https://dl.bintray.com/kotlin/kotlinx" )
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                val rejected = listOf("eap", "alpha", "beta", "rc", "cr", "m", "preview")
                    .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                    .any { it.matches(candidate.version) }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
    // optional parameters
    checkForGradleUpdate = true
}