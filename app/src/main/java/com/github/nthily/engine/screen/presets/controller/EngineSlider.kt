package com.github.nthily.engine.screen.presets.controller

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun EngineSlider(
  modifier: Modifier = Modifier,
  onValueChanged: (Float) -> Unit
) {
  var animateOffsetY by remember { mutableStateOf(0f) }
  Canvas(
    modifier = modifier
      .rotate(180f)
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = {
            animateOffsetY = it.y
          }
        )
      }
      .pointerInput(Unit) {
        detectVerticalDragGestures { change, _ ->
          animateOffsetY = when {
            change.position.y < 0f -> 0f
            else -> change.position.y
          }
          onValueChanged(animateOffsetY.coerceIn(0f, 1f))
        }
      }
  ) {
    val canvasSize = size
    if (animateOffsetY >= canvasSize.height) animateOffsetY = canvasSize.height
    drawRoundRect(
      color = Color.Gray,
      size = Size(canvasSize.width + extraRectSize.toPx(), canvasSize.height + extraRectSize.toPx()),
      cornerRadius = CornerRadius(20.dp.toPx()),
    )
    drawRoundRect(
      color = Color.White,
      size = canvasSize,
      cornerRadius = CornerRadius(10.dp.toPx()),
      topLeft = Offset(extraRectSize.toPx() / 2, extraRectSize.toPx() / 2)
    )
    drawRoundRect(
      color = Color(0xFF4959A6),
      size = Size(canvasSize.width, animateOffsetY),
      cornerRadius = CornerRadius(10.dp.toPx()),
      topLeft = Offset(extraRectSize.toPx() / 2, extraRectSize.toPx() / 2)
    )
    drawSliderIndicator(
      topLeft = Offset(-(sliderIndicatorExtendWidth.toPx() / 2 - extraRectSize.toPx() / 2), animateOffsetY)
    )
  }
}

fun DrawScope.drawSliderIndicator(topLeft: Offset) {
  val slideSize = size
  drawRoundRect(
    color = Color(0xffD9D9D9),
    size = Size(slideSize.width + (sliderIndicatorExtendWidth.toPx()), sliderIndicatorHeight.toPx()),
    topLeft = topLeft,
    cornerRadius = CornerRadius(10.dp.toPx())
  )
}

val extraRectSize = 25.dp
val sliderIndicatorHeight = 35.dp
val sliderIndicatorExtendWidth = 50.dp
