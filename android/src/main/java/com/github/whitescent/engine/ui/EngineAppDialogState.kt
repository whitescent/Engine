package com.github.whitescent.engine.ui

import androidx.compose.runtime.*

@Composable
fun rememberEngineAppDialogState(): EngineAppDialogState {
  return remember { EngineAppDialogState() }
}

@Stable
class EngineAppDialogState {
  var shouldShowNewPresetDialog by mutableStateOf(false)
    private set

  var shouldShowHelpDialog by mutableStateOf(false)
    private set

  fun setShowNewPresetDialog(shouldShow: Boolean) {
    shouldShowNewPresetDialog = shouldShow
  }

  fun setShowHelpDialog(shouldShow: Boolean) {
    shouldShowHelpDialog = shouldShow
  }

}
