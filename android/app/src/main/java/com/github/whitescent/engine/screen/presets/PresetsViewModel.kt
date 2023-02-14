package com.github.whitescent.engine.screen.presets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.repository.PresetsRepository
import com.github.whitescent.engine.data.repository.UserDataRepository
import com.github.whitescent.engine.utils.GameCategory
import com.github.whitescent.engine.utils.TextErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PresetsViewModel @Inject constructor(
  userDataRepository: UserDataRepository,
  private val presetsRepository: PresetsRepository
) : ViewModel() {

  private val hideDetails = userDataRepository.userData.map { it.hideDetails }
  private val inputText = MutableStateFlow("")
  private val inputTextManager = MutableStateFlow(InputTextManager())

  val presetList = presetsRepository.presetList
  val sortingPreference = presetsRepository.sortingPreference

  val uiState: StateFlow<PresetsUiState> =
    combine(
      hideDetails,
      inputTextManager
    ) { hideDetails, inputTextManager ->
      PresetsUiState(
        hideDetails = hideDetails,
        inputTextManager = inputTextManager
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = PresetsUiState()
    )

  init {
    viewModelScope.launch {
      inputText
        .debounce(450)
        .filterNot(String::isEmpty)
        .collectLatest { input ->
          if (input.length > 30)
            inputTextManager.value = inputTextManager.value.copy(
              isTextError = true,
              error = TextErrorType.LengthLimited,
              isTyping = false
            )
          else {
            if (presetsRepository.isExistedInPresetList(input)) {
              inputTextManager.value = inputTextManager.value.copy(
                isTextError = true,
                error = TextErrorType.NameExisted,
                isTyping = false
              )
            }
          }
        }
    }
  }
  fun onValueChange(text: String) {
    inputText.update { text }
    inputTextManager.value = inputTextManager.value.copy(text = text, isTyping = true, isTextError = false)
  }

  fun onClickSortCategory(index: Int) =
    presetsRepository.updateSortingPreferenceByCategory(index)

  fun onSortingChanged() =
    presetsRepository.updateSortingPreferenceByAscending()

  fun deletePreset(presetModel: PresetModel) =
    presetsRepository.deletePreset(presetModel)

  fun onConfirmed(gameCategory: GameCategory) {
    val currentMoment = Clock.System.now().toEpochMilliseconds()
    val presetName = inputText.value
    presetsRepository.addPreset(
      presetName = presetName,
      gameCategory = gameCategory,
      currentMoment = currentMoment
    )
    resetInputTextManager()
  }
  fun resetInputTextManager() {
    inputTextManager.value = inputTextManager.value.copy(text = "", isTyping = false, isTextError = false)
  }
}

data class InputTextManager(
  val text: String = "",
  val isTextError: Boolean = false,
  val isTyping: Boolean = false,
  val error: TextErrorType? = null,
)

data class PresetsUiState(
  val inputTextManager: InputTextManager = InputTextManager(),
  val hideDetails: Boolean = false
)
