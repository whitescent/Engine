package com.github.nthily.engine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.nthily.engine.ui.component.AppScaffold
import com.github.nthily.engine.ui.theme.EngineTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      EngineTheme {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()
        SideEffect {
          systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
          )
        }
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "app") {
          composable("app") {
            AppScaffold()
          }
        }
      }
    }
  }
}

typealias AppTheme = MaterialTheme
