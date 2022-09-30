package com.github.nthily.engine.screen.config

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.nthily.engine.AppTheme
import com.github.nthily.engine.R

@Composable
fun ConfigRoot(
  viewModel: ConfigViewModel = hiltViewModel()
) {
  val dialogState by viewModel.dialogState.collectAsState()
  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = stringResource(id = R.string.no_config_file),
      style = AppTheme.typography.headlineSmall,
      modifier = Modifier.align(Alignment.Center)
    )
    ExtendedFloatingActionButton(
      onClick = viewModel::onClickFab,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
    ) {
      Icon(Icons.Rounded.Edit, null)
      Spacer(modifier = Modifier.padding(horizontal = 4.dp))
      Text(stringResource(id = R.string.add_new_config))
    }
  }
  ConfigDialog(
    state = dialogState,
    onDismissRequest = viewModel::onDismissRequest,
    onConfirmed = viewModel::onConfirmed,
    onValueChange = viewModel::onValueChange
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigDialog(
  state: ConfigDialogState,
  onDismissRequest: () -> Unit,
  onConfirmed: () -> Unit,
  onValueChange: (String) -> Unit
) {
  if (state.display) {
    AlertDialog(
      onDismissRequest = onDismissRequest,
      title = {
        Text(
          text = stringResource(id = R.string.add_new_config),
          style = AppTheme.typography.headlineMedium
        )
      },
      text = {
        OutlinedTextField(
          value = state.text,
          onValueChange = onValueChange,
          label = { Text(stringResource(id = R.string.config_name)) }
        )
      },
      dismissButton = {
        TextButton(
          onClick = onDismissRequest
        ) {
          Text(stringResource(id = R.string.cancel))
        }
      },
      confirmButton = {
        TextButton(
          onClick = onConfirmed,
          enabled = state.text != ""
        ) {
          Text(stringResource(id = R.string.add))
        }
      }
    )
  }
}
