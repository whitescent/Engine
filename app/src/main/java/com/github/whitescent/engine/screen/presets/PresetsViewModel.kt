package com.github.whitescent.engine.screen.presets

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.R
import com.github.whitescent.engine.data.model.PresetsModel
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

  private val _mmkv = MutableStateFlow(MMKV.defaultMMKV())
  val mmkv = _mmkv.asStateFlow()

  private val dialogUi = MutableStateFlow(PresetsDialogUiState())
  private val inputText = MutableStateFlow("")
  val dialogUiState = dialogUi.asStateFlow()

  init {
    viewModelScope.launch {
      inputText
        .debounce(500)
        .filterNot(String::isEmpty)
        .collectLatest {
          if (_mmkv.value.containsKey(it)) {
            dialogUi.value = dialogUi.value.copy(isTextError = true)
          }
        }
    }
  }

  fun onClickFab() {
    dialogUi.value = PresetsDialogUiState(display = true)
  }

  fun onDismissRequest() {
    dialogUi.value = PresetsDialogUiState()
  }

  fun onValueChange(text: String) {
    inputText.update { text }
    dialogUi.value = PresetsDialogUiState(true, text, false)
  }

  fun onConfirmed(gameItem: GameItem) {
    try {
      val currentMoment = Clock.System.now().toEpochMilliseconds()
      val presetsName = dialogUi.value.text
      _mmkv.value.encode(presetsName, PresetsModel(presetsName, gameItem, currentMoment))
      _mmkv.value = MMKV.defaultMMKV()
      dialogUi.value = PresetsDialogUiState()
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
  val display: Boolean = false,
  val text: String = "",
  val isTextError: Boolean = false
)
