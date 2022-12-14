package com.github.nthily.engine.screen.presets.editor

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.github.nthily.engine.sensor.AbstractSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val sensor: AbstractSensor
) : ViewModel(){
  
  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  val controllerList = mutableStateListOf<ControllerUiModel>()

  fun startListeningSensor() {
    sensor.startListening()
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
  }
  fun stopListeningSensor() = sensor.stopListening()

  fun updateControllerList(controllerType: ControllerType) {
    controllerList.add(ControllerUiModel(
      100, 100, controllerType, UUID.randomUUID()
    ))
  }

}

enum class ControllerType {
  RoundButton, RectangularButton, Axis
}

data class ControllerUiModel(
  val offsetX: Int,
  val offsetY: Int,
  val controllerType: ControllerType,
  val uuid: UUID
)
