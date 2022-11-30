package com.github.nthily.engine.screen.presets.controller

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class AxisOrientation {
  Vertical, Horizontal
}

@Composable
fun EngineAxis(
  modifier: Modifier = Modifier,
  orientation: AxisOrientation = AxisOrientation.Vertical,
  onValueChanged: (Float) -> Unit
) {
  var animateOffsetY by remember { mutableStateOf(0f) }
  Canvas(
    modifier = Modifier
      .graphicsLayer {
        rotationZ = if (orientation == AxisOrientation.Vertical) 180f else 270f
        transformOrigin = TransformOrigin(0f, 0f)
      }
      .layout { measurable, constraints ->
        val placeable = measurable.measure(
          Constraints(
            minWidth = constraints.minHeight,
            maxWidth = constraints.maxHeight,
            minHeight = constraints.minWidth,
            maxHeight = constraints.maxHeight,
          )
        )
        val strokeSize = (placeable.width * 0.18f).roundToInt()
//        val sliderIndicatorHeight = ((placeable.height * 0.1f).dp.value).roundToInt()
//        val sliderIndicatorWidth = ((placeable.width * 0.6f).dp.value).roundToInt()
        layout(placeable.height, placeable.width) {
          placeable.place(-placeable.width, strokeSize)
        }
//        val strokeSize = (placeable.width * 0.18f).roundToInt()
//        val sliderIndicatorHeight = ((placeable.height * 0.1f).dp.value).roundToInt()
//        val sliderIndicatorWidth = ((placeable.width * 0.6f).dp.value).roundToInt()
//        layout(placeable.height, placeable.width) {
//          if (orientation == SliderOrientation.Horizontal)
//            placeable.place(-placeable.width - sliderIndicatorWidth / 2, strokeSize)
//          else
//            placeable.place(
//              -placeable.width - sliderIndicatorWidth / 2 - strokeSize / 2,
//              -placeable.height - sliderIndicatorHeight - strokeSize * 2
//            )
//        }
      }
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
      .then(modifier)
  ) {
    val canvasSize = size
    if (animateOffsetY >= canvasSize.height) animateOffsetY = canvasSize.height
    drawRoundRect(
      color = Color(0xFF4959A6),
      size = Size(canvasSize.width, animateOffsetY),
      cornerRadius = CornerRadius(10.dp.toPx())
    )
    drawRoundRect(
      color = Color.Gray,
      size = Size(canvasSize.width, canvasSize.height),
      style = Stroke(canvasSize.width * 0.18f),
      cornerRadius = CornerRadius(10.dp.toPx()),
    )
    drawAxisIndicator(offsetY = animateOffsetY)
  }
}

fun DrawScope.drawAxisIndicator(offsetY: Float) {
  val sliderSize = size
  val axisIndicatorHeight = (sliderSize.height * 0.1f).dp
  val axisIndicatorWidth = (sliderSize.width * 0.6f).dp
  drawRoundRect(
    color = Color(0xffD9D9D9),
    size = Size(axisIndicatorWidth.toPx(), axisIndicatorHeight.toPx()),
    topLeft = Offset(-(axisIndicatorWidth.toPx() / 2) + sliderSize.width / 2, offsetY),
    cornerRadius = CornerRadius(10.dp.toPx())
  )
}
