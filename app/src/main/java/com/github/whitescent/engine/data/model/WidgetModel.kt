package com.github.whitescent.engine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetModel(
  val position: Position,
  val widgetType: WidgetType
) : Parcelable

@Parcelize
data class Position(
  val offsetX: Float,
  val offsetY: Float,
  val scale: Float
) : Parcelable

enum class WidgetType {
  RoundButton, RectangularButton, Axis
}
