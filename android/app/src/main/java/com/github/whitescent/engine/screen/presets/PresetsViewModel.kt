package com.github.whitescent.engine.screen.presets

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.model.PresetListModel
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortPreferenceModel
import com.github.whitescent.engine.utils.GameCategory
import com.github.whitescent.engine.utils.TextErrorType
import com.github.whitescent.engine.utils.getSortedPresetList
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
    getLatestPresetList()
    viewModelScope.launch {
      inputText
        .debounce(450)
        .filterNot(String::isEmpty)
        .collectLatest { input ->
          if (input.length > 30)
            _uiState.value = _uiState.value.copy(isTextError = true, error = TextErrorType.LengthLimited, isTyping = false)
          else {
            presetList
              .find {
                it.name == input
              }?.let {
                _uiState.value = _uiState.value.copy(isTextError = true, error = TextErrorType.NameExisted, isTyping = false)
              }
          }
        }
    }
  }

  fun getLatestPresetList() {
    val hideDetails = mmkv.decodeBool("hide_preset_details")
    // get sort preference
    mmkv.decodeParcelable("sort_preference", SortPreferenceModel::class.java)?.let {
      sort.value = it
    }
    // get all presets
    mmkv.decodeParcelable("preset_list", PresetListModel::class.java)?.let {
      presetList = getSortedPresetList(it.value, sort.value)
    }
    _uiState.value = _uiState.value.copy(hideDetails = hideDetails)
  }

  fun onClickFab() {
    _uiState.value = _uiState.value.copy(openNewPresetDialog = true)
  }

  fun onDismissRequest() {
    _uiState.value = _uiState.value.copy(openNewPresetDialog = false, text = "", isTextError = false)
  }

  fun onValueChange(text: String) {
    inputText.update { text }
    _uiState.value = _uiState.value.copy(text = text, isTyping = true, isTextError = false)
  }

  fun onClickSortCategory(index: Int) {
    sort.value = sort.value.copy(selectedSortCategory = index)
    mmkv.encode("sort_preference", sort.value)
    presetList = getSortedPresetList(presetList, sort.value)
  }
  fun onSortingChanged() {
    sort.value = sort.value.copy(isAscending = !sort.value.isAscending)
    mmkv.encode("sort_preference", sort.value)
    presetList = getSortedPresetList(presetList, sort.value)
  }

  fun deletePreset(presetModel: PresetModel) {
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
      presetList = getSortedPresetList(presetList, sort.value)
      mmkv.encode("preset_list", PresetListModel(presetList))
      // reset uiState
      _uiState.value = _uiState.value.copy(openNewPresetDialog = false, text = "", isTextError = false)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun openHelpDialog() {
    _uiState.value = _uiState.value.copy(openHelpDialog = true)
  }

  fun closeHelpDialog() {
    _uiState.value = _uiState.value.copy(openHelpDialog = false)
  }

}

data class PresetsUiState(
  val openNewPresetDialog: Boolean = false,
  val openHelpDialog: Boolean = false,
  val text: String = "",
  val isTextError: Boolean = false,
  val isTyping: Boolean = false,
  val error: TextErrorType? = null,
  val hideDetails: Boolean = false
)
