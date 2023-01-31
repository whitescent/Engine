package com.github.whitescent.engine.screen.presets.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

enum class AxisOrientation {
  Vertical, Horizontal
}

@Composable
fun EngineAxis(
  modifier: Modifier = Modifier,
  orientation: AxisOrientation = AxisOrientation.Vertical,
  enabledScroll: Boolean = true,
  onPress: () -> Unit = { },
  onValueChanged: (Float) -> Unit
) {
  var animateOffsetY by remember { mutableStateOf(0f) }
  var canvasHeight by remember { mutableStateOf(0f) }
  val gestureModifier = Modifier
    .pointerInput(Unit) {
      detectTapGestures(
        onPress = {
          animateOffsetY = -canvasHeight+it.y
          onValueChanged(animateOffsetY)
          onPress()
        },
      )
    }
    .pointerInput(Unit) {
      detectVerticalDragGestures { _, dragAmount ->
        animateOffsetY = -abs(animateOffsetY + dragAmount)
        onValueChanged(animateOffsetY)
      }
    }
  Canvas(
    modifier = Modifier
      .rotate(if(orientation == AxisOrientation.Horizontal) 90f else 0f)
      .layout { measurable, constraints ->
        val placeable = measurable.measure(
          Constraints(
            minWidth = constraints.minHeight,
            maxWidth = constraints.maxHeight,
            minHeight = constraints.minWidth,
            maxHeight = constraints.maxHeight,
          )
        )
        val axisIndicatorWidth = ((placeable.width * 0.6f).dp.value).roundToInt()
        val strokeSize = (placeable.width * 0.18f).roundToInt()
        layout(placeable.height, placeable.width) {
          if (orientation == AxisOrientation.Horizontal)
            placeable.place(placeable.height / 3, -axisIndicatorWidth-strokeSize)
          else
            placeable.place(placeable.width / 2, -axisIndicatorWidth)
        }
      }
      .then(modifier)
      .then(if(enabledScroll) gestureModifier else Modifier)
  ) {
    val canvasSize = size
    canvasHeight = canvasSize.height
    if (abs(animateOffsetY) >= canvasSize.height) animateOffsetY = -canvasSize.height
    drawRoundRect(
      color = Color(0xFF4959A6),
      size = Size(canvasSize.width, -abs(animateOffsetY)),
      topLeft = Offset(0f, size.height),
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

private fun DrawScope.drawAxisIndicator(offsetY: Float) {
  val sliderSize = size
  val axisIndicatorHeight = (sliderSize.height * 0.06f).dp
  val axisIndicatorWidth = (sliderSize.width * 0.6f).dp
  drawRoundRect(
    color = Color(0xffD9D9D9),
    size = Size(axisIndicatorWidth.toPx(), axisIndicatorHeight.toPx()),
    topLeft = Offset(
      x = -(axisIndicatorWidth.toPx() / 2) + sliderSize.width / 2,
      y = size.height + offsetY - axisIndicatorHeight.value * 2
    ),
    cornerRadius = CornerRadius(10.dp.toPx())
  )
}
