package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.github.whitescent.engine.sensor.AbstractSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val sensor: AbstractSensor
) : ViewModel() {
  
  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  var widgetList = mutableStateListOf<WidgetUiModel>()

  fun startListeningSensor() {
    sensor.startListening()
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
  }
  fun stopListeningSensor() = sensor.stopListening()

  fun addNewWidget(widgetType: WidgetType) {
    widgetList.add(WidgetUiModel(
      Position(0f, 0f, 1f), widgetType, UUID.randomUUID()
    ))
  }

  fun updateControllerPos(index: Int, position: Position) {
    widgetList[index] = widgetList[index].copy(position = position)
  }

}

enum class WidgetType {
  RoundButton, RectangularButton, Axis
}

data class WidgetUiModel(
  val position: Position,
  val widgetType: WidgetType,
  val uuid: UUID
)

data class Position(
  val offsetX: Float,
  val offsetY: Float,
  val scale: Float
)
