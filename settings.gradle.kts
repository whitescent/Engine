pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
  plugins {
    val kotlinVersion = extra["kotlin.version"] as String
    val composeVersion = extra["compose.version"] as String
    kotlin("multiplatform").version(kotlinVersion)
    id("org.jetbrains.compose").version(composeVersion)
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Engine"
include("android:app")
include("desktop")
