package com.github.whitescent.engine.screen.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.repository.PresetsRepository
import com.github.whitescent.engine.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ConnectionViewModel @Inject constructor(
  presetsRepository: PresetsRepository,
  private val userDataRepository: UserDataRepository
) : ViewModel() {

  private val inputText = MutableStateFlow("")
  private val hostnameError = MutableStateFlow(false)

  val connectionUiState: StateFlow<ConnectionUiState> =
    combine(
      presetsRepository.presetList,
      userDataRepository.userData,
      hostnameError
    ) { presetList, userData, hostnameError ->
      val selectedPreset = when (userData.selectedPreset) {
        null -> {
          if (presetList.isNotEmpty()) {
            presetList[0]
          } else {
            null
          }
        }
        else -> {
          if (presetList.contains(userData.selectedPreset)) userData.selectedPreset
          else if (presetList.isNotEmpty()) presetList[0]
          // If it's not included,
          // it means the user just deleted the selected preset from the presetList,
          // in that case, we default to using the first preset in the presetList
          else null
        }
      }
      ConnectionUiState(
        hostname = userData.hostname,
        selectedPreset = selectedPreset,
        presetList = presetList,
        hostnameError = hostnameError
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = ConnectionUiState()
    )

  init {
    viewModelScope.launch {
      inputText
        .debounce(500)
        .filterNot(String::isEmpty)
        .collect { input ->
          val isValid = input.matches(hostPattern)
          if (isValid) {
            hostnameError.emit(false)
            userDataRepository.updateHostnameValue(input)
          }
          else hostnameError.emit(true)
        }
    }
  }
  fun updateSelectedPreset(presetModel: PresetModel) =
    userDataRepository.updateSelectedPreset(presetModel)
  fun updateHostName(name: String) {
    inputText.update { name }
    userDataRepository.updateHostnameValue(name)
  }

}

const val port = 12345
private val hostPattern =
  "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$".toRegex()

data class ConnectionUiState(
  val hostname: String = "",
  val hostnameError: Boolean = false,
  val selectedPreset: PresetModel? = null,
  val presetList: List<PresetModel> = listOf()
)
