package com.github.whitescent.engine.screen.connect

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Connect(
  viewModel: ConnectionViewModel = hiltViewModel()
) {
  Button(
    onClick = {
      viewModel.connect()
    }
  ) {

  }
}
