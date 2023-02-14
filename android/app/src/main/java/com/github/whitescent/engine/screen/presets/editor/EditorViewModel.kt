package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.github.whitescent.engine.data.model.*
import com.github.whitescent.engine.data.repository.PresetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val presetsRepository: PresetsRepository
) : ViewModel() {

  private val presetList = presetsRepository.presetList

  var widgetList by mutableStateOf<List<WidgetModel>>(listOf())

  fun readWidgetList(presetsName: String) {
    widgetList = presetList.value.first { it.name == presetsName }.widgetList
  }

  fun saveWidgetList(presetsName: String) {
    val newPreset = presetList.value
      .first {
        it.name == presetsName
      }
      .copy(widgetList = widgetList)
    val newPresetList = presetList.value
      .map {
        if (it.name == presetsName) newPreset
        else it
      }
    presetsRepository.updatePreset(PresetListModel(newPresetList))
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
