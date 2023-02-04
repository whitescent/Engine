package com.github.whitescent.engine.screen.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.whitescent.engine.data.model.PresetListModel
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortPreferenceModel
import com.github.whitescent.engine.utils.getSortedPresetList
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor() : ViewModel() {

  private val mmkv = MMKV.defaultMMKV()

  private val _uiState = MutableStateFlow(ConnectionUiState())
  val uiState = _uiState.asStateFlow()

  private var presetList by mutableStateOf<List<PresetModel>>(listOf())
  private var selectedPreset by mutableStateOf<PresetModel?>(null)

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
    _uiState.value = _uiState.value.copy(navigateToConsole = false, errorMessage = null)
  }
  fun updateHostName(name: String) {
    _uiState.value = _uiState.value.copy(hostname = name)
    mmkv.encode("hostname", name)
  }
}

const val port = 12345
data class ConnectionUiState(
  val hostname: String = "",
  val selectedPreset: PresetModel? = null,
  val presets: List<PresetModel> = listOf(),
  val errorMessage: String? = null,
  val navigateToConsole: Boolean = false
)
