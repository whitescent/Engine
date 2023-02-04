package com.github.whitescent.engine.screen.presets

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.R
import com.github.whitescent.engine.data.model.PresetListModel
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortPreferenceModel
import com.github.whitescent.engine.utils.sortPresetList
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PresetsViewModel @Inject constructor() : ViewModel() {

  private val mmkv = MMKV.defaultMMKV()

  var presetList by mutableStateOf<List<PresetModel>>(listOf())

  private val _uiState = MutableStateFlow(PresetsUiState())
  private val inputText = MutableStateFlow("")
  val uiState = _uiState.asStateFlow()

  private val sort = MutableStateFlow(SortPreferenceModel())
  val sortPreference = sort.asStateFlow()

  init {
    getLatestMMKVValue()
    viewModelScope.launch {
      inputText
        .debounce(500)
        .filterNot(String::isEmpty)
        .collectLatest { input ->
          if (input.length > 50) _uiState.value = _uiState.value.copy(isTextError = true)
          presetList
            .find {
              it.name == input
            }?.let {
              _uiState.value = _uiState.value.copy(isTextError = true)
            }
        }
    }
  }

  fun getLatestMMKVValue() {
    val hideDetails = mmkv.decodeBool("hide_preset_details")
    // get sort preference
    mmkv.decodeParcelable("sort_preference", SortPreferenceModel::class.java)?.let {
      sort.value = it
    }
    // get all presets
    mmkv.decodeParcelable("preset_list", PresetListModel::class.java)?.let {
      presetList = sortPresetList(it.value, sort.value)
    }
    _uiState.value = _uiState.value.copy(hideDetails = hideDetails)
  }

  fun onClickFab() {
    _uiState.value = _uiState.value.copy(openDialog = true)
  }

  fun onDismissRequest() {
    _uiState.value = _uiState.value.copy(openDialog = false)
  }

  fun onValueChange(text: String) {
    inputText.update { text }
    _uiState.value = _uiState.value.copy(text = text)
  }

  fun onClickSortCategory(index: Int) {
    sort.value = sort.value.copy(selectedSortCategory = index)
    mmkv.encode("sort_preference", sort.value)
    presetList = sortPresetList(presetList, sort.value)
  }
  fun onSortingChanged() {
    sort.value = sort.value.copy(isAscending = !sort.value.isAscending)
    mmkv.encode("sort_preference", sort.value)
    presetList = sortPresetList(presetList, sort.value)
  }

  fun deletePresets(presetModel: PresetModel) {
    presetList = presetList.toMutableList().also {
      it.remove(presetModel)
    }
    mmkv.encode("preset_list", PresetListModel(presetList))
  }

  fun onConfirmed(gameCategory: GameCategory) {
    try {
      val currentMoment = Clock.System.now().toEpochMilliseconds()
      val presetName = _uiState.value.text
      presetList = presetList.toMutableList().also {
        it.add(PresetModel(presetName, gameCategory, currentMoment))
      }
      presetList = sortPresetList(presetList, sort.value)
      mmkv.encode("preset_list", PresetListModel(presetList))
      // reset uiState
      _uiState.value = _uiState.value.copy(openDialog = false, text = "", isTextError = false)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

}

enum class GameCategory(
  val painter: Int,
  @StringRes val gameName: Int
) {
  Undefined(R.drawable.other_presets, R.string.uncategorized),
  AssettoCorsa(R.drawable.assetto_corsa, R.string.assetto_corsa),
  Forza(R.drawable.forza, R.string.forza_series),
  F1(R.drawable.f1, R.string.f1_series),
  Dirt(R.drawable.dirt, R.string.dirt_series)
}

data class PresetsUiState(
  val openDialog: Boolean = false,
  val text: String = "",
  val isTextError: Boolean = false,
  val hideDetails: Boolean = false
)
