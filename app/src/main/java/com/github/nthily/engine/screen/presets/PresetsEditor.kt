package com.github.nthily.engine.screen.presets

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.github.nthily.engine.data.model.PresetsModel
import com.github.nthily.engine.utils.LocalSystemUiController
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PresetsEditor(
  orientation: Int,
  presetsModel: PresetsModel
) {
  val context = LocalContext.current
  val systemUiController = LocalSystemUiController.current
  systemUiController.systemBarsBehavior =
    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
  DisposableEffect(Unit) {
    val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = orientation
    systemUiController.isSystemBarsVisible = false
    onDispose {
      activity.requestedOrientation = originalOrientation
      systemUiController.isSystemBarsVisible = true
    }
  }
  Box(Modifier.fillMaxSize(), Alignment.Center) {
    Text(
      text = presetsModel.presetsName
    )
  }
}

fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}
