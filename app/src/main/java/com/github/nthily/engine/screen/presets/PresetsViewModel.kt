package com.github.nthily.engine.screen.presets

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.github.nthily.engine.R
import com.github.nthily.engine.data.model.PresetsModel
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor() : ViewModel() {

  private val _mmkv = MutableStateFlow(MMKV.defaultMMKV())
  val mmkv = _mmkv.asStateFlow()

  private val _dialogState = MutableStateFlow(PresetsDialogUiState(false, ""))
  val dialogState = _dialogState.asStateFlow()

  fun onClickFab() {
    _dialogState.value = PresetsDialogUiState(true, "")
  }

  fun onDismissRequest() {
    _dialogState.value = PresetsDialogUiState(false, "")
  }

  fun onValueChange(text: String) {
    _dialogState.value = PresetsDialogUiState(true, text)
  }

  fun onConfirmed(gameItem: GameItem) {
    try {
      val currentMoment = Clock.System.now().toEpochMilliseconds()
      val presetsName = _dialogState.value.text
      _mmkv.value.encode(presetsName, PresetsModel(presetsName, gameItem, currentMoment))
      _mmkv.value = MMKV.defaultMMKV()
      _dialogState.value = PresetsDialogUiState(false, "")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

}

enum class GameItem(
  val painter: Int,
  @StringRes val gameName: Int
) {
  Undefined(R.drawable.other_presets, R.string.undefined_game),
  AssettoCorsa(R.drawable.assetto_corsa, R.string.assetto_corsa),
  Forza(R.drawable.forza, R.string.forza_series),
  F1(R.drawable.f1, R.string.f1_series),
  Dirt(R.drawable.dirt, R.string.dirt_series)
}

data class PresetsDialogUiState(
  val display: Boolean,
  val text: String
)
