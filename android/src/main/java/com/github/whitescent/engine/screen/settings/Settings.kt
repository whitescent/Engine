package com.github.whitescent.engine.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.BuildConfig
import com.github.whitescent.engine.R
import com.github.whitescent.engine.destinations.AboutLibraryDestination
import com.github.whitescent.engine.ui.theme.LocalThemeManager
import com.github.whitescent.engine.utils.NightModeType
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun Settings(
  viewModel: SettingsViewModel = hiltViewModel(),
  navigator: DestinationsNavigator
) {
  val state by viewModel.settingsUiState.collectAsState()
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
        updateHidePresetDetailsValue = viewModel::updateHidePresetDetailsValue,
        updateButtonVibrationEffectValue = viewModel::updateButtonVibrationEffectValue
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
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GeneralSettings(
  state: UserEditableSettings,
  updateVolumeButtonValue: (Boolean) -> Unit,
  updateHidePresetDetailsValue: (Boolean) -> Unit,
  updateButtonVibrationEffectValue: (Boolean) -> Unit
) {
  var openMenu by remember { mutableStateOf(false) }
  val themeManager = LocalThemeManager.current
  PrimarySettingsText(stringResource(id = R.string.general))
  ListItem(
    headlineContent = {
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
    headlineContent = {
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
    headlineContent = {
      Text(
        text = stringResource(id = R.string.Button_vibration_effect),
        style = AppTheme.typography.titleMedium
      )
    },
    trailingContent = {
      Switch(
        checked = state.buttonVibration,
        onCheckedChange = { updateButtonVibrationEffectValue(it) },
      )
    },
    leadingContent = {
      Icon(Icons.Rounded.Vibration, null, modifier = Modifier.size(24.dp))
    }
  )
  Column {
    ListItem(
      headlineContent = {
        Text(
          text = stringResource(id = R.string.night_mode),
          style = AppTheme.typography.titleMedium
        )
      },
      supportingContent = {
        Text(
          text = when (themeManager.nightMode) {
            NightModeType.LIGHT -> stringResource(id = R.string.off)
            NightModeType.NIGHT -> stringResource(id = R.string.on)
            NightModeType.FOLLOW_SYSTEM -> stringResource(id = R.string.follow_system)
          },
          style = AppTheme.typography.labelMedium
        )
      },
      leadingContent = {
        AnimatedContent(
          targetState = themeManager.nightMode,
          transitionSpec = {
            slideInVertically { height -> height } + fadeIn() with
              slideOutVertically { height -> - height } + fadeOut() using(SizeTransform(clip = false))
          }
        ) {
          when (themeManager.nightMode) {
            NightModeType.NIGHT -> Icon(Icons.Rounded.DarkMode, null)
            NightModeType.LIGHT -> Icon(Icons.Rounded.LightMode, null)
            NightModeType.FOLLOW_SYSTEM -> {
              if (isSystemInDarkTheme())
                Icon(Icons.Rounded.DarkMode, null)
              else Icon(Icons.Rounded.LightMode, null)
            }
          }
        }
      },
      modifier = Modifier.clickable {
        openMenu = true
      }
    )
    DropdownMenu(
      expanded = openMenu,
      onDismissRequest = { openMenu = false }
    ) {
      DropdownMenuItem(
        text = { Text(stringResource(id = R.string.off)) },
        onClick = {
          themeManager.updateNightMode(NightModeType.LIGHT)
          openMenu = false
        }
      )
      DropdownMenuItem(
        text = { Text(stringResource(id = R.string.on)) },
        onClick = {
          themeManager.updateNightMode(NightModeType.NIGHT)
          openMenu = false
        }
      )
      DropdownMenuItem(
        text = { Text(stringResource(id = R.string.follow_system)) },
        onClick = {
          themeManager.updateNightMode(NightModeType.FOLLOW_SYSTEM)
          openMenu = false
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherSettings(
  navigate: () -> Unit
) {

  val context = LocalContext.current
  val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

  PrimarySettingsText(stringResource(id = R.string.other))
  ListItem(
    headlineContent = {
      Text(
        text = stringResource(id = R.string.app_name),
        style = AppTheme.typography.titleMedium
      )
    },
    supportingContent = {
      Text(
        text = BuildConfig.VERSION_NAME,
        style = AppTheme.typography.labelMedium
      )
    },
    modifier = Modifier.clickable {
      launchCustomChromeTab(
        context = context,
        uri = Uri.parse("https://github.com/whitescent/Engine/releases"),
        toolbarColor = backgroundColor
      )
    },
    leadingContent = {
      Icon(painterResource(id = R.drawable.github), null)
    }
  )
  ListItem(
    headlineContent = {
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

@Composable
fun PrimarySettingsText(text: String) =
  ListItem(
    headlineContent = {
      Text(
        text = text,
        style = AppTheme.typography.bodyLarge,
        color = AppTheme.colorScheme.primary
      )
    }
  )

fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
  val customTabBarColor = CustomTabColorSchemeParams.Builder()
    .setToolbarColor(toolbarColor).build()
  val customTabsIntent = CustomTabsIntent.Builder()
    .setDefaultColorSchemeParams(customTabBarColor)
    .build()

  customTabsIntent.launchUrl(context, uri)
}
