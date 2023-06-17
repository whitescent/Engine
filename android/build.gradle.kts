plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("dagger.hilt.android.plugin")
  id("kotlin-kapt")
  id("kotlin-parcelize")
  id("com.mikepenz.aboutlibraries.plugin")
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.github.whitescent.engine"
  compileSdk = 33

  defaultConfig {
    applicationId = "com.github.whitescent.engine"
    minSdk = 21
    targetSdk = 33
    versionCode = 5
    versionName = "1.0.4"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
  buildFeatures {
    compose = true
  }
  packagingOptions {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }
  applicationVariants.all {
    kotlin.sourceSets {
      getByName(name) {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
      }
    }
  }
}

dependencies {

  // core
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.startup.runtime)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.browser)
  coreLibraryDesugaring(libs.android.desugar)

  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.util)
  implementation(libs.androidx.compose.ui.tooling.preview)

  // material design
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)

  // accompanist
  implementation(libs.accompanist.systemUiController)
  implementation(libs.accompanist.pager)
  implementation(libs.accompanist.flowlayout)

  // aboutLibraries
  implementation(libs.aboutLibraries.core)
  implementation(libs.aboutLibraries.compose)

  // compose destinations
  implementation(libs.compose.destinations.core)
  ksp(libs.compose.destinations.ksp)

  // mmkv
  implementation(libs.mmkv)

  // kotlinx datetime
  implementation(libs.kotlinx.datetime)

  // ktor
  implementation(libs.ktor.network)

  // navigation
  implementation(libs.androidx.navigation.compose)

  // hilt
  implementation(libs.com.google.dagger.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  kapt(libs.com.google.dagger.hilt.compiler)

  // test
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}