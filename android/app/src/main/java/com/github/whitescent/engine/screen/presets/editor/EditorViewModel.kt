package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.github.whitescent.engine.data.model.*
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor() : ViewModel() {

  private val mmkv = MMKV.defaultMMKV()

  var widgetList by mutableStateOf<List<WidgetModel>>(listOf())

  fun readWidgetList(presetsName: String) {
    val presets = mmkv
      .decodeParcelable("preset_list", PresetListModel::class.java)!!.value
      .first {
        it.name == presetsName
      }
    widgetList = presets.widgetList.toMutableList()
  }

  fun saveWidgetList(presetsName: String) {
    val newPreset = mmkv
      .decodeParcelable("preset_list", PresetListModel::class.java)!!.value
      .first {
        it.name == presetsName
      }
      .copy(widgetList = widgetList)
    val newPresetList = mmkv
      .decodeParcelable("preset_list", PresetListModel::class.java)!!.value
      .map {
        if (it.name == presetsName) newPreset
        else it
      }
    mmkv.encode("preset_list", PresetListModel(newPresetList))
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

  fun deleteWidget(widget: WidgetModel) {
    widgetList = widgetList.toMutableList().also {
      it.remove(widget)
    }
  }

  fun updateWidgetPos(index: Int, position: Position) {
    widgetList = widgetList.toMutableList().also {
      it[index] = widgetList[index].copy(position = position)
    }
  }
}
