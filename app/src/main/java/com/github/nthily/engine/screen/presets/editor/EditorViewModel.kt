package com.github.nthily.engine.screen.presets.editor

import androidx.lifecycle.ViewModel
import com.github.nthily.engine.sensor.AbstractSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val sensor: AbstractSensor
) : ViewModel(){

  private val _dialogState = MutableStateFlow(false)
  val dialogState = _dialogState.asStateFlow()

  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  fun startListeningSensor() {
    sensor.startListening()
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
  }

  fun stopListeningSensor() = sensor.stopListening()

  fun onClickLabel() {
    _dialogState.value = true
  }

  fun onDismissRequest() {
    _dialogState.value = false
  }

}
