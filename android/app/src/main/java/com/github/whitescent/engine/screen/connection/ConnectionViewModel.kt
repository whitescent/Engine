package com.github.whitescent.engine.screen.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.model.PresetListModel
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortPreferenceModel
import com.github.whitescent.engine.utils.getSortedPresetList
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ConnectionViewModel @Inject constructor() : ViewModel() {

  private val mmkv = MMKV.defaultMMKV()

  private val _uiState = MutableStateFlow(ConnectionUiState())
  private val inputText = MutableStateFlow("")
  val uiState = _uiState.asStateFlow()

  private var presetList by mutableStateOf<List<PresetModel>>(listOf())
  private var selectedPreset by mutableStateOf<PresetModel?>(null)

  init {
    viewModelScope.launch {
      inputText
        .debounce(500)
        .filterNot(String::isEmpty)
        .collect { input ->
          val isValid = input.matches(hostPattern)
          if (isValid) {
            _uiState.value = _uiState.value.copy(hostnameError = false)
            mmkv.encode("hostname", input)
          }
          else _uiState.value = _uiState.value.copy(hostnameError = true)
        }
    }
  }

  fun initUiState() {
    val hostname = mmkv.decodeString("hostname") // get saved hostname if it existed
    presetList = getSavedPresetList()
    selectedPreset =
      when (val savedSelectedPreset = mmkv.decodeParcelable("selected_preset", PresetModel::class.java)) {
        null -> {
          // if the selected preset is not stored locally, the first item in the preset list is used
          if (presetList.isNotEmpty()) {
            presetList[0]
          } else {
            null
          }
        }
        else -> {
          if(presetList.contains(savedSelectedPreset)) savedSelectedPreset
          else if (presetList.isNotEmpty()) presetList[0]
          else null
        }
      }
    // init uiState
    _uiState.value = _uiState.value.copy(
      hostname = hostname ?: "",
      selectedPreset = selectedPreset,
      presets = presetList
    )
  }

  private fun getSavedPresetList(): List<PresetModel> {
    val savedList = mmkv.decodeParcelable("preset_list", PresetListModel::class.java)
    savedList?.let {
      if (it.value.isNotEmpty()) {
        val preference = mmkv.decodeParcelable("sort_preference", SortPreferenceModel::class.java)
        return getSortedPresetList(it.value, preference)
      }
    }
    return emptyList()
  }
  fun updateSelectedPreset(presetModel: PresetModel) {
    _uiState.value = _uiState.value.copy(selectedPreset = presetModel)
    mmkv.encode("selected_preset", presetModel)
  }
  fun resetUiState() {
    _uiState.value = _uiState.value.copy(navigateToConsole = false)
  }
  fun updateHostName(name: String) {
    inputText.update { name }
    _uiState.value = _uiState.value.copy(hostname = name)
  }
}

const val port = 12345
private val hostPattern =
  "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$".toRegex()
data class ConnectionUiState(
  val hostname: String = "",
  val hostnameError: Boolean = false,
  val selectedPreset: PresetModel? = null,
  val presets: List<PresetModel> = listOf(),
  val navigateToConsole: Boolean = false
)
