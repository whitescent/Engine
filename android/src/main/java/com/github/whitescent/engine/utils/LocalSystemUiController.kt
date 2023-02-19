package com.github.whitescent.engine.utils

import androidx.compose.runtime.compositionLocalOf
import com.google.accompanist.systemuicontroller.SystemUiController

val LocalSystemUiController = compositionLocalOf<SystemUiController> {
  error("CompositionLocal LocalNavController not present")
}
