package com.github.whitescent.engine.screen.settings

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

  private val mmkv = MMKV.defaultMMKV()
  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState = _uiState.asStateFlow()

  init {
    val volumeButtonEnabled = mmkv.decodeBool("volume_button_enabled")
    val hideDetails = mmkv.decodeBool("hide_preset_details")
    _uiState.value = SettingsUiState(
      volumeButtonEnabled = volumeButtonEnabled,
      hideDetails = hideDetails
    )
  }

  fun updateVolumeButtonValue(value: Boolean) {
    _uiState.value = _uiState.value.copy(volumeButtonEnabled = value)
    mmkv.encode("volume_button_enabled", value)
  }

  fun updateHidePresetDetailsValue(value: Boolean) {
    _uiState.value = _uiState.value.copy(hideDetails = value)
    mmkv.encode("hide_preset_details", value)
  }

}

data class SettingsUiState(
  val volumeButtonEnabled: Boolean = false,
  val hideDetails: Boolean = false
)
