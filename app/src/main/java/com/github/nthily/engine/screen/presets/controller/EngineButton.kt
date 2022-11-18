package com.github.nthily.engine.screen.presets.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.github.nthily.engine.AppTheme

@Composable
fun EngineButton(
  modifier: Modifier = Modifier,
  shape: Shape = CircleShape
) {
  Box(
    modifier = modifier
      .clickable(onClick = { })
      .background(AppTheme.colorScheme.secondaryContainer, shape = shape)
  ) {
  
  }
}
