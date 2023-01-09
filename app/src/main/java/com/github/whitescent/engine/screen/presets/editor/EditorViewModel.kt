package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.github.whitescent.engine.data.model.Position
import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.data.model.WidgetModel
import com.github.whitescent.engine.data.model.WidgetType
import com.github.whitescent.engine.sensor.AbstractSensor
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val sensor: AbstractSensor
) : ViewModel() {
  
  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  private val _mmkv = MMKV.defaultMMKV()

  var widgetList by mutableStateOf<List<WidgetModel>>(listOf())

  fun startListeningSensor() {
    sensor.startListening()
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
  }
  fun stopListeningSensor() = sensor.stopListening()

  fun readWidgetList(presetsName: String) {
    val presets = _mmkv.decodeParcelable(presetsName, PresetsModel::class.java)!!
    widgetList = presets.widgetList.toMutableList()
  }

  fun saveWidgetList(presetsName: String) {
    val presets = _mmkv.decodeParcelable(presetsName, PresetsModel::class.java)
    _mmkv.encode(presetsName, presets!!.copy(
      widgetList = widgetList
    ))
  }

  fun addNewWidget(widgetType: WidgetType) {
    widgetList = widgetList.toMutableList().also {
      it.add(
        WidgetModel(
          position = Position(0f, 0f, 1f),
          widgetType = widgetType
        )
      )
    }
  }

  fun updateWidgetPos(index: Int, position: Position) {
    widgetList = widgetList.toMutableList().also {
      it[index] = widgetList[index].copy(position = position)
    }
  }
}
