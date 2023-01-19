package com.github.whitescent.engine.screen.presets

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.R
import com.github.whitescent.engine.data.model.PresetsListModel
import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.data.model.SortPreferenceModel
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

  var presetsList by mutableStateOf<List<PresetsModel>>(listOf())

  private val dialogUi = MutableStateFlow(PresetsDialogUiState())
  private val inputText = MutableStateFlow("")
  val dialogUiState = dialogUi.asStateFlow()

  private val sort = MutableStateFlow(SortPreferenceModel())
  val sortPreference = sort.asStateFlow()

  init {
    // get all presets
    if (mmkv.containsKey("presets_list")) {
      presetsList = mmkv.decodeParcelable("presets_list", PresetsListModel::class.java)!!.value
    }
    // get sort preference
    if (mmkv.containsKey("sort_preference")) {
      sort.value = mmkv.decodeParcelable("sort_preference", SortPreferenceModel::class.java)!!
    }
    viewModelScope.launch {
      inputText
        .debounce(500)
        .filterNot(String::isEmpty)
        .collectLatest { input ->
          presetsList
            .find {
              it.presetsName == input
            }?.let {
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

  fun onClickSortCategory(index: Int) {
    sort.value = sort.value.copy(selectedSortCategory = index)
    mmkv.encode("sort_preference", sort.value)
  }
  fun onSortingChanged() {
    sort.value = sort.value.copy(isAscending = !sort.value.isAscending)
    mmkv.encode("sort_preference", sort.value)
  }

  fun updateSorting() {
    val newList = when (sort.value.selectedSortCategory) {
      0 -> {
        if(sort.value.isAscending) presetsList.sortedBy { it.presetsName }
        else presetsList.sortedByDescending { it.presetsName }
      }
      1 -> {
        if(sort.value.isAscending) presetsList.sortedBy { it.gameType }
        else presetsList.sortedByDescending { it.gameType }
      }
      2 -> {
        if(sort.value.isAscending) presetsList.sortedBy { it.createdAt }
        else presetsList.sortedByDescending { it.createdAt }
      }
      else -> presetsList
    }
    presetsList = newList
  }

  fun onConfirmed(gameItem: GameItem) {
    try {
      val currentMoment = Clock.System.now().toEpochMilliseconds()
      val presetsName = dialogUi.value.text
      presetsList = presetsList.toMutableList().also {
        it.add(PresetsModel(presetsName, gameItem, currentMoment))
      }
      mmkv.encode("presets_list", PresetsListModel(presetsList))
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
