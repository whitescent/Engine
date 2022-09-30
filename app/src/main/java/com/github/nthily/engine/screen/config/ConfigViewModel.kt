package com.github.nthily.engine.screen.config

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(

) : ViewModel() {
  private val _dialogState = MutableStateFlow(ConfigDialogState(false, ""))
  val dialogState = _dialogState.asStateFlow()

  fun onClickFab() {
    _dialogState.value = ConfigDialogState(true, "")
  }

  fun onDismissRequest() {
    _dialogState.value = ConfigDialogState(false, "")
  }

  fun onValueChange(text: String) {
    _dialogState.value = ConfigDialogState(true, text)
  }

  fun onConfirmed() {
    _dialogState.value = ConfigDialogState(false, "")
  }

}

data class ConfigDialogState(
  val display: Boolean,
  val text: String
)
