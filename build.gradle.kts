import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        google()
        maven ("https://plugins.gradle.org/m2/")
        maven ("https://dl.bintray.com/kotlin/kotlin-eap" )
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Lib.Versions.gradleBuildTools}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Lib.Versions.kotlin}")
        classpath("gradle.plugin.com.wiredforcode:gradle-spawn-plugin:${Lib.Versions.gradleSpawn}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Lib.Versions.kotlin}")
    }
}

plugins {
    id("com.github.ben-manes.versions") version Lib.Versions.versionsPlugin
}

allprojects {
    repositories {
        maven ( "https://dl.bintray.com/kotlin/kotlinx" )
        google()
        jcenter()
        maven ( "https://dl.bintray.com/kotlin/kotlin-eap" )
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