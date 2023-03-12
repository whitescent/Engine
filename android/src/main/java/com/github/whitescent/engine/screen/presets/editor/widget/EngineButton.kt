package com.github.whitescent.engine.screen.presets.editor.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import com.github.whitescent.engine.AppTheme

@Composable
fun EngineButton(
  modifier: Modifier = Modifier,
  shape: Shape = CircleShape,
  onDoubleTap: (() -> Unit)? = null,
  onPress: (() -> Unit)? = null,
  onTap: (() -> Unit)? = null,
) {
  val tapGesture = when(onDoubleTap) {
    null -> Modifier.pointerInput(Unit) {
      detectTapGestures(
        onTap = {
          onTap?.invoke()
        },
        onPress = {
          onPress?.invoke()
        }
      )
    }
    else -> Modifier.pointerInput(Unit) {
      detectTapGestures(
        onTap = {// Tap is onClick
          onTap?.invoke()
        },
        onPress = {
          onPress?.invoke()
        },
        onDoubleTap = {
          onDoubleTap()
        }
      )
    }
  }
  Box(
    modifier = modifier
      .clip(shape)
      .background(AppTheme.colorScheme.primary)
      .then(tapGesture)
  )
}
