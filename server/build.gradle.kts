buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Lib.Versions.kotlin}")
        classpath("com.google.cloud.tools:appengine-gradle-plugin:2.4.5")
    }
}

apply {
    plugin("kotlin")
    plugin("war")
    plugin("com.google.cloud.tools.appengine")
}

plugins {
    java
    kotlin("jvm")
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/ktor" )
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Lib.Versions.kotlin}")
    implementation("io.ktor:ktor-server-servlet:${Lib.Versions.ktor}")
    implementation("io.ktor:ktor-html-builder:${Lib.Versions.ktor}")
    implementation("io.ktor:ktor-gson:${Lib.Versions.ktor}")
    implementation("com.google.cloud:google-cloud-logging-logback:0.116.0-alpha")
    implementation("com.google.cloud:google-cloud-datastore:2.14.7")
    implementation("com.google.appengine:appengine:1.9.98")
}


task("run") { dependsOn("appengineRun") }