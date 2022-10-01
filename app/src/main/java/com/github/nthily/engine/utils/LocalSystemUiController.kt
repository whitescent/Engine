package com.github.nthily.engine.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController

val LocalSystemUiController = compositionLocalOf<SystemUiController> {
  error("CompositionLocal LocalNavController not present")
}
