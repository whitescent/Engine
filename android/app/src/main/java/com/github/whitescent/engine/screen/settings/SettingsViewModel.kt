package com.github.whitescent.engine.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val userDataRepository: UserDataRepository
) : ViewModel() {

  val settingsUiState: StateFlow<UserEditableSettings> =
    userDataRepository.userData.map {
      UserEditableSettings(
        volumeButtonEnabled = it.volumeButtonEnabled,
        hideDetails = it.hideDetails,
        buttonVibration = it.buttonVibration
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = UserEditableSettings()
    )

  fun updateVolumeButtonValue(value: Boolean) =
    userDataRepository.updateVolumeButtonValue(value)

  fun updateHidePresetDetailsValue(value: Boolean) =
    userDataRepository.updateHidePresetDetailsValue(value)

  fun updateButtonVibrationEffectValue(value: Boolean) =
    userDataRepository.updateButtonVibrationEffectValue(value)
}

data class UserEditableSettings(
  val volumeButtonEnabled: Boolean = false,
  val hideDetails: Boolean = false,
  val buttonVibration: Boolean = false
)
