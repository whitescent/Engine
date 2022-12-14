package com.github.nthily.engine.screen.presets.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.github.nthily.engine.AppTheme

@Composable
fun EngineButton(
  modifier: Modifier = Modifier,
  shape: Shape = CircleShape,
  onClick: () -> Unit = {  }
) {
  var flag by remember { mutableStateOf(false) }
  
  val otherModifier = if (flag) Modifier.scale(0.85f) else Modifier
  
  Box(
    modifier = modifier
      .then(otherModifier)
      .background(AppTheme.colorScheme.primary, shape = shape)
      .clickable (
        onClick = onClick,
        indication = null,
        interactionSource = MutableInteractionSource()
      )
//      .pointerInput(Unit) {
//        detectTapGestures(
//          onTap = {
//            flag = false
//          },
//          onPress = {
//            flag = true
//          }
//        )
//      }
  )
}
