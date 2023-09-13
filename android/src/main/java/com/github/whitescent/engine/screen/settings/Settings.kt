package com.github.whitescent.engine.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.BuildConfig
import com.github.whitescent.engine.R
import com.github.whitescent.engine.destinations.AboutLibraryDestination
import com.github.whitescent.engine.screen.presets.H4Text
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
            (slideInVertically { height -> height } + fadeIn())
              .togetherWith(slideOutVertically { height -> -height } + fadeOut()) using(SizeTransform(clip = false))
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun OtherSettings(
  navigate: () -> Unit
) {
  val context = LocalContext.current
  val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
  var openDialog by remember { mutableStateOf(false) }

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
        uri = Uri.parse("https://github.com/whitescent/Engine"),
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
        text = stringResource(id = R.string.tutorial),
        style = AppTheme.typography.titleMedium
      )
    },
    modifier = Modifier
      .clickable {
        openDialog = true
      },
    leadingContent = {
      Icon(Icons.Rounded.Help, null, modifier = Modifier.size(24.dp))
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
  if (openDialog) {
    AlertDialog(
      title = {
        Text(
          text = stringResource(id = R.string.tutorial),
          style = AppTheme.typography.headlineMedium
        )
      },
      text = {
        val text = buildAnnotatedString {
          H4Text(stringResource(id = R.string.help1))
          append("\n\n")
          H4Text(stringResource(id = R.string.help2))
          append("\n\n")
          H4Text(stringResource(id = R.string.help3))
          append("\n\n")
          pushStringAnnotation(
            tag = "URL", annotation = "https://github.com/whitescent/Engine/releases"
          )
          withStyle(
            SpanStyle(
              color = Color(0xFF1B7CFF),
              textDecoration = TextDecoration.Underline,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          ) {
            append("EngineServer")
            append("\n\n")
          }
          pop()
          H4Text(stringResource(id = R.string.help4))
          append("\n\n")
          pushStringAnnotation(
            tag = "URL", annotation = "https://github.com/whitescent/Engine#-usage"
          )
          withStyle(
            SpanStyle(
              color = Color(0xFF1B7CFF),
              textDecoration = TextDecoration.Underline,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          ) {
            H4Text(stringResource(id = R.string.help5))
          }
        }
        ClickableText(
          text = text,
          onClick = { offset ->
            text.getStringAnnotations(
              tag = "URL", start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
              launchCustomChromeTab(
                context = context,
                uri = Uri.parse(annotation.item),
                toolbarColor = backgroundColor
              )
            }
          }
        )
      },
      onDismissRequest = { openDialog = false },
      confirmButton = {
        TextButton(
          onClick = { openDialog = false },
        ) {
          Text(stringResource(id = R.string.yes))
        }
      },
    )
  }
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
