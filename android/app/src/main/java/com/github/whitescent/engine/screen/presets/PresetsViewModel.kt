package com.github.whitescent.engine.screen.presets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortingPreferenceModel
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

  private val _hideDetails = userDataRepository.userData.map { it.hideDetails }
  private val _inputText = MutableStateFlow("")
  private val _inputTextManager = MutableStateFlow(InputTextManager())

  val presetList = presetsRepository.presetList
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = listOf()
    )
  val sortingPreference = presetsRepository.sortingPreference
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = SortingPreferenceModel()
    )

  val uiState: StateFlow<PresetsUiState> =
    combine(
      _hideDetails,
      _inputTextManager
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
      _inputText
        .debounce(450)
        .filterNot(String::isEmpty)
        .collectLatest { input ->
          if (input.length > 30)
            _inputTextManager.value = _inputTextManager.value.copy(
              isTextError = true,
              error = TextErrorType.LengthLimited,
              isTyping = false
            )
          else {
            if (presetsRepository.isExistedInPresetList(input)) {
              _inputTextManager.value = _inputTextManager.value.copy(
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
    _inputText.update { text }
    _inputTextManager.value = _inputTextManager.value.copy(text = text, isTyping = true, isTextError = false)
  }

  fun onClickSortCategory(index: Int) =
    presetsRepository.updateSortingPreferenceByCategory(index)

  fun onSortingChanged() =
    presetsRepository.updateSortingPreferenceByAscending()

  fun deletePreset(presetModel: PresetModel) =
    presetsRepository.deletePreset(presetModel)

  fun onConfirmed(gameCategory: GameCategory) {
    val currentMoment = Clock.System.now().toEpochMilliseconds()
    val presetName = _inputText.value
    presetsRepository.addPreset(
      presetName = presetName,
      gameCategory = gameCategory,
      currentMoment = currentMoment
    )
    resetInputTextManager()
  }
  fun resetInputTextManager() {
    _inputTextManager.value = _inputTextManager.value.copy(text = "", isTyping = false, isTextError = false)
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
