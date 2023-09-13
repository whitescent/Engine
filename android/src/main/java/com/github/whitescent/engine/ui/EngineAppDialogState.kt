package com.github.whitescent.engine.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
