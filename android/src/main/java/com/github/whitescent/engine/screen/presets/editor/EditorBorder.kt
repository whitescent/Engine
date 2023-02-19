package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun Modifier.onEditingBorder() =
  this.then(
    Modifier.drawWithCache {
      onDrawWithContent {
        drawContent()
        drawRect(
          color = Color(63, 164, 237),
          style = Stroke(lineStrokeWidth),
          topLeft = Offset(-rectangleWidth / 2, -rectangleHeight / 2),
          size = Size(rectangleWidth, rectangleHeight)
        )
        drawLine(
          start = Offset(rectangleWidth / 2, 0f),
          end = Offset(size.width - rectangleWidth / 2, 0f),
          color = Color(63, 164, 237),
          strokeWidth = lineStrokeWidth
        )
        drawRect(
          color = Color(63, 164, 237),
          style = Stroke(lineStrokeWidth),
          topLeft = Offset(-rectangleWidth / 2 + size.width, -rectangleHeight / 2),
          size = Size(rectangleWidth, rectangleHeight)
        )
        drawLine(
          start = Offset(0f, rectangleHeight / 2),
          end = Offset(0f, size.height - rectangleHeight / 2),
          color = Color(63, 164, 237),
          strokeWidth = lineStrokeWidth
        )
        drawRect(
          color = Color(63, 164, 237),
          style = Stroke(lineStrokeWidth),
          topLeft = Offset(-rectangleWidth / 2, size.height - rectangleHeight / 2),
          size = Size(rectangleWidth, rectangleHeight)
        )
        drawLine(
          start = Offset(rectangleWidth / 2, size.height),
          end = Offset(size.width - rectangleWidth / 2, size.height),
          color = Color(63, 164, 237),
          strokeWidth = lineStrokeWidth
        )
        drawRect(
          color = Color(63, 164, 237),
          style = Stroke(lineStrokeWidth),
          topLeft = Offset(size.width - rectangleWidth / 2, size.height - rectangleHeight / 2),
          size = Size(rectangleWidth, rectangleHeight)
        )
        drawLine(
          start = Offset(size.width, rectangleHeight / 2),
          end = Offset(size.width, size.height - rectangleHeight / 2),
          color = Color(63, 164, 237),
          strokeWidth = lineStrokeWidth
        )
      }
    }
  )

private const val lineStrokeWidth = 4f

private val DrawScope.rectangleWidth
  get() = size.width * 0.05f

private val DrawScope.rectangleHeight
  get() = size.height * 0.05f
