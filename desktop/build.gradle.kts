import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
}

group = "com.github.whitescent"
version = "1.0.1"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "11"
    }
    withJava()
  }
  sourceSets {
    commonMain {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
      }
    }
    val jvmMain by getting {
      val ktor_version = "2.2.2"
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(compose.materialIconsExtended)
        implementation(compose.material3)
        implementation("io.ktor:ktor-network:$ktor_version")
        implementation(files("libs/jna-platform.jar"))
        implementation(files("libs/jna.jar"))
      }
    }
    val jvmTest by getting
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"
    nativeDistributions {
      buildTypes.release {
        proguard {
          configurationFiles.from("compose-desktop.pro")
        }
      }
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      windows {
        iconFile.set(project.file("icon.ico"))
      }
      packageName = "EngineServer"
      packageVersion = "1.0.1"
      vendor = "WhiteScent dev"
      copyright = "© 2023 WhiteScent. All rights reserved."
      appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
    }
  }
}
