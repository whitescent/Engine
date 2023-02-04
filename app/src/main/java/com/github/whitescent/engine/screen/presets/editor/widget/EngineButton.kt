package com.github.whitescent.engine.screen.presets.editor.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.github.whitescent.engine.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EngineButton(
  modifier: Modifier = Modifier,
  shape: Shape = CircleShape,
  onDoubleClick: (() -> Unit)? = null,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(shape)
      .background(AppTheme.colorScheme.primary)
      .combinedClickable(
        onClick = onClick,
        onDoubleClick = onDoubleClick
      )
  )
}
