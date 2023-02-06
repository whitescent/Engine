package com.github.whitescent.engine.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.github.whitescent.engine.utils.NightModeType
import com.github.whitescent.engine.utils.ThemeManager

val LocalThemeManager = compositionLocalOf {
  ThemeManager()
}

private val DarkColorScheme = darkColorScheme(
  primary = Purple80,
  secondary = PurpleGrey80,
  tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = Pink40
)


@Composable
fun EngineTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  val themeManager = ThemeManager()
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      when(themeManager.nightMode) {
        NightModeType.NIGHT -> dynamicDarkColorScheme(context)
        NightModeType.LIGHT -> dynamicLightColorScheme(context)
        NightModeType.FOLLOW_SYSTEM -> {
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
      }
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
      ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
    }
  }
  CompositionLocalProvider(
    LocalThemeManager provides themeManager
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
