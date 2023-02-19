plugins {
  kotlin("multiplatform") apply false
  id("org.jetbrains.compose") apply false
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://plugins.gradle.org/m2/")
  }
}

buildscript {
  dependencies {
    classpath(libs.android.gradle.plugin)
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.hilt.gradle.plugin)
    classpath(libs.aboutLibraries.plugin)
  }
}