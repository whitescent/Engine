package com.github.whitescent.engine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.github.whitescent.engine.ui.theme.EngineTheme
import com.github.whitescent.engine.ui.theme.LocalThemeManager
import com.github.whitescent.engine.utils.LocalSystemUiController
import com.github.whitescent.engine.utils.NightModeType
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      EngineTheme {
        val systemUiController = rememberSystemUiController()
        val themeManager = LocalThemeManager.current
        val useDarkIcons = when (themeManager.nightMode) {
          NightModeType.NIGHT -> false
          NightModeType.LIGHT -> true
          NightModeType.FOLLOW_SYSTEM -> !isSystemInDarkTheme()
        }
        SideEffect {
          systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
          )
        }
        CompositionLocalProvider(
          LocalSystemUiController provides systemUiController
        ) {
          DestinationsNavHost(navGraph = AppNavGraphs.root)
        }
      }
    }
  }
}

typealias AppTheme = MaterialTheme
