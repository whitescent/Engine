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
  
  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  fun startListeningSensor() {
    sensor.startListening()
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
  }

  fun stopListeningSensor() = sensor.stopListening()
  
}
