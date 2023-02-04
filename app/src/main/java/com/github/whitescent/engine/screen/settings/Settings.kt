package com.github.whitescent.engine.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.BuildConfig
import com.github.whitescent.engine.R
import com.github.whitescent.engine.destinations.AboutLibraryDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun Settings(
  viewModel: SettingsViewModel = hiltViewModel(),
  navigator: DestinationsNavigator
) {
  val state by viewModel.uiState.collectAsState()
  LazyColumn(
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      SettingsTopBar()
    }
    item {
      GeneralSettings(
        state = state,
        updateVolumeButtonValue = viewModel::updateVolumeButtonValue,
        updateHidePresetDetailsValue = viewModel::updateHidePresetDetailsValue
      )
    }
    item {
      OtherSettings { navigator.navigate(AboutLibraryDestination()) }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
  TopAppBar(
    title = {
      Text(
        text = stringResource(id = R.string.settings)
      )
    }
  )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettings(
  state: SettingsUiState,
  updateVolumeButtonValue: (Boolean) -> Unit,
  updateHidePresetDetailsValue: (Boolean) -> Unit
) {
  PrimarySettingsText(stringResource(id = R.string.general))
  ListItem(
    headlineText = {
      Text(
        text = stringResource(id = R.string.volume_button_enabled),
        style = AppTheme.typography.titleMedium
      )
    },
    trailingContent = {
      Switch(
        checked = state.volumeButtonEnabled,
        onCheckedChange = { updateVolumeButtonValue(it) }
      )
    },
    leadingContent = {
      Icon(painterResource(id = R.drawable.gamepad), null, modifier = Modifier.size(24.dp))
    }
  )
  ListItem(
    headlineText = {
      Text(
        text = stringResource(id = R.string.hide_preset_details),
        style = AppTheme.typography.titleMedium
      )
    },
    trailingContent = {
      Switch(
        checked = state.hideDetails,
        onCheckedChange = { updateHidePresetDetailsValue(it) },
      )
    },
    leadingContent = {
      Icon(Icons.Rounded.VisibilityOff, null, modifier = Modifier.size(24.dp))
    }
  )
  ListItem(
    headlineText = {
      Text(
        text = stringResource(id = R.string.night_mode),
        style = AppTheme.typography.titleMedium
      )
    },
    supportingText = {
      Text(
        text = stringResource(id = R.string.follow_system),
        style = AppTheme.typography.labelMedium
      )
    },
    leadingContent = {
      when (isSystemInDarkTheme()) {
        true -> Icon(Icons.Rounded.DarkMode, null)
        else -> Icon(Icons.Rounded.LightMode, null)
      }
    },
    modifier = Modifier.clickable { },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherSettings(
  navigate: () -> Unit
) {
  val uriHandler = LocalUriHandler.current
  PrimarySettingsText(stringResource(id = R.string.other))
  ListItem(
    headlineText = {
      Text(
        text = stringResource(id = R.string.app_name),
        style = AppTheme.typography.titleMedium
      )
    },
    supportingText = {
      Text(
        text = BuildConfig.VERSION_NAME,
        style = AppTheme.typography.labelMedium
      )
    },
    modifier = Modifier.clickable {
      uriHandler.openUri("https://github.com/whitescent/Engine")
    },
    leadingContent = {
      Icon(painterResource(id = R.drawable.github), null)
    }
  )
  ListItem(
    headlineText = {
      Text(
        text = stringResource(id = R.string.licenses),
        style = AppTheme.typography.titleMedium
      )
    },
    modifier = Modifier.clickable {
      navigate()
    },
    leadingContent = {
      Icon(Icons.Rounded.Description, null, modifier = Modifier.size(24.dp))
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimarySettingsText(text: String) =
  ListItem(
    headlineText = {
      Text(
        text = text,
        style = AppTheme.typography.bodyLarge,
        color = AppTheme.colorScheme.primary
      )
    }
  )