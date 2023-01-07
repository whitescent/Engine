package com.github.whitescent.engine.screen.presets.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.github.whitescent.engine.AppTheme

@Composable
fun EngineButton(
  modifier: Modifier = Modifier,
  shape: Shape = CircleShape,
  onClick: () -> Unit = {  }
) {
  Box(
    modifier = modifier
      .background(AppTheme.colorScheme.primary, shape = shape)
      .clickable (
        onClick = onClick,
        indication = null,
        interactionSource = MutableInteractionSource()
      )
  )
}
