package com.github.whitescent.engine.data.repository

import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.UserDataModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor() {

  private val mmkv = MMKV.defaultMMKV()
  val userData = MutableStateFlow(UserDataModel())
  init {
    userData.value = mmkv.decodeParcelable("user_settings_preference", UserDataModel::class.java)
      ?: UserDataModel()
  }
  fun updateVolumeButtonValue(value: Boolean) {
    userData.update { it.copy(volumeButtonEnabled = value) }
    mmkv.encode("user_settings_preference", userData.value)
  }

  fun updateHidePresetDetailsValue(value: Boolean) {
    userData.update { it.copy(hideDetails = value) }
    mmkv.encode("user_settings_preference", userData.value)
  }

  fun updateButtonVibrationEffectValue(value: Boolean) {
    userData.update { it.copy(buttonVibration = value) }
    mmkv.encode("user_settings_preference", userData.value)
  }

  fun updateHostnameValue(name: String) {
    userData.update { it.copy(hostname = name) }
    mmkv.encode("user_settings_preference", userData.value)
  }

  fun updateSelectedPreset(preset: PresetModel) {
    userData.update { it.copy(selectedPreset = preset) }
    mmkv.encode("user_settings_preference", userData.value)
  }

}
