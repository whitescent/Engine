package com.github.whitescent.engine.screen.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.model.PresetsListModel
import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.data.model.SortPreferenceModel
import com.github.whitescent.engine.utils.sortPresetList
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor() : ViewModel() {

  private val mmkv = MMKV.defaultMMKV()

  private val _uiState = MutableStateFlow(ConnectionUiState())
  val uiState = _uiState.asStateFlow()

  private var presetsList by mutableStateOf<List<PresetsModel>>(listOf())
  private var selectedPreset by mutableStateOf<PresetsModel?>(null)

  fun initUiState() {
    val hostname = mmkv.decodeString("hostname") // get saved hostname if it existed
    presetsList = getSavedPresetList()
    selectedPreset =
      when (val savedSelectedPreset = mmkv.decodeParcelable("selected_preset", PresetsModel::class.java)) {
        null -> {
          // if the selected preset is not stored locally, the first item in the preset list is used
          if (presetsList.isNotEmpty()) {
            presetsList[0]
          } else {
            null
          }
        }
        else -> {
          if(presetsList.contains(savedSelectedPreset)) savedSelectedPreset
          else if (presetsList.isNotEmpty()) presetsList[0]
          else null
        }
      }
    // init uiState
    _uiState.value = _uiState.value.copy(
      hostname = hostname ?: "",
      selectedPreset = selectedPreset,
      presets = presetsList
    )
  }

  private fun getSavedPresetList(): List<PresetsModel> {
    val savedList = mmkv.decodeParcelable("presets_list", PresetsListModel::class.java)
    savedList?.let {
      if (it.value.isNotEmpty()) {
        val preference = mmkv.decodeParcelable("sort_preference", SortPreferenceModel::class.java)
        return sortPresetList(it.value, preference)
      }
    }
    return emptyList()
  }
  fun updateSelectedPreset(presetsModel: PresetsModel) {
    _uiState.value = _uiState.value.copy(selectedPreset = presetsModel)
    mmkv.encode("selected_preset", presetsModel)
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
  val selectedPreset: PresetsModel? = null,
  val presets: List<PresetsModel> = listOf(),
  val errorMessage: String? = null,
  val navigateToConsole: Boolean = false
)
