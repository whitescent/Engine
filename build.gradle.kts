plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.multiplatform) apply false
  alias(libs.plugins.jetbrains.compose) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.aboutLibraries) apply false
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://plugins.gradle.org/m2/")
  }
}
