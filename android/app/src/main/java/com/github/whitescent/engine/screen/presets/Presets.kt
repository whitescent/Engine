package com.github.whitescent.engine.screen.presets

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.whitescent.engine.R
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.destinations.EditorDestination
import com.github.whitescent.engine.ui.component.CenterRow
import com.github.whitescent.engine.ui.component.HeightSpacer
import com.github.whitescent.engine.ui.component.WidthSpacer
import com.github.whitescent.engine.utils.GameCategory
import com.github.whitescent.engine.utils.TextErrorType
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.datetime.*

@Composable
fun Presets(
  viewModel: PresetsViewModel = hiltViewModel(),
  onFabClick: () -> Unit,
  onHelpButtonClick: () -> Unit,
  navigator: DestinationsNavigator
) {
  val state by viewModel.uiState.collectAsState()
  val sortingPreference by viewModel.sortingPreference.collectAsState()
  val presetList by viewModel.presetList.collectAsState()
  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    PresetsTopBar(
      preference = sortingPreference,
      onClickSortCategory = viewModel::onClickSortCategory,
      onSortingChanged = viewModel::onSortingChanged,
      openHelpDialog = onHelpButtonClick
    )
    PresetList(
      hideDetails = state.hideDetails,
      presetList = presetList,
      navigateToEditor = {
        navigator.navigate(
          EditorDestination(
            orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            presetModel = it
          )
        )
      },
      deletePreset = viewModel::deletePreset
    )
  }
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomEnd
  ) {
    ExtendedFloatingActionButton(
      onClick = onFabClick,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
    ) {
      Icon(Icons.Rounded.Add, null)
      WidthSpacer(value = 4.dp)
      Text(text = stringResource(id = R.string.add_new_preset))
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PresetList(
  hideDetails: Boolean,
  presetList: List<PresetModel>,
  navigateToEditor: (PresetModel) -> Unit,
  deletePreset: (PresetModel) -> Unit
) {
  AnimatedContent(presetList.size) {
    when(it) {
      0 -> {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
          CenterRow {
            Icon(Icons.Rounded.Construction, null, modifier = Modifier
              .size(40.dp)
              .alpha(0.5f))
            WidthSpacer(value = 6.dp)
            Text(
              text = stringResource(id = R.string.no_preset_files),
              style = AppTheme.typography.headlineSmall,
              modifier = Modifier.alpha(0.5f)
            )
          }
        }
      }
      else -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
        ) {
          items(presetList) {preset ->
            key(preset.createdAt) {
              PresetListItem(hideDetails, preset, navigateToEditor, deletePreset)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetListItem(
  hideDetails: Boolean,
  presetModel: PresetModel,
  navigateToEditor: (PresetModel) -> Unit,
  deletePreset: (PresetModel) -> Unit
) {
  val time = Instant.fromEpochMilliseconds(presetModel.createdAt).toLocalDateTime(TimeZone.UTC)
  val name = presetModel.name
  val gameCategory = presetModel.gameCategory

  val timeString = "${time.date.month.number}-${time.date.dayOfMonth}"

  var openMenu by remember { mutableStateOf(false) }

  CenterRow {
    ListItem(
      headlineText = {
        Column {
          Text(
            text = name,
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSecondaryContainer,
            overflow = TextOverflow.Ellipsis
          )
          HeightSpacer(value = 4.dp)
        }
      },
      supportingText = {
        if (!hideDetails) {
          CenterRow {
            Icon(Icons.Rounded.Widgets, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            WidthSpacer(value = 2.dp)
            Text(
              text = presetModel.widgetList.size.toString(),
              style = AppTheme.typography.labelLarge,
              color = AppTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.alpha(0.5f)
            )
            WidthSpacer(value = 6.dp)
            Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            WidthSpacer(value = 2.dp)
            Text(
              text = timeString,
              style = AppTheme.typography.labelLarge,
              color = AppTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.alpha(0.5f)
            )
          }
        }
      },
      leadingContent = {
        if (gameCategory != GameCategory.Undefined) {
          Image(
            painter = painterResource(id = gameCategory.painter),
            contentDescription = null,
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
          )
        } else {
          Icon(
            imageVector = Icons.Rounded.Description,
            contentDescription = null,
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape),
            tint = AppTheme.colorScheme.onSurface
          )
        }
      },
      trailingContent = {
        IconButton(
          onClick = { openMenu = true }
        ) {
          Icon(Icons.Rounded.MoreHoriz, null)
        }
        DropdownMenu(
          expanded = openMenu,
          onDismissRequest = { openMenu = false }
        ) {
          DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.delete_preset)) },
            onClick = { deletePreset(presetModel) },
            leadingIcon = {
              Icon(Icons.Rounded.Delete, null)
            }
          )
        }
      },
      modifier = Modifier.clickable { navigateToEditor(presetModel) }
    )
  }
}

@Composable
fun NewPresetDialog(
  onDismiss: () -> Unit,
  viewModel: PresetsViewModel = hiltViewModel()
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  NewPresetDialog(
    state = state,
    onDismissRequest = {
      onDismiss()
      viewModel.resetInputTextManager()
    },
    onConfirmed = viewModel::onConfirmed,
    onValueChange = viewModel::onValueChange
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NewPresetDialog(
  state: PresetsUiState,
  onDismissRequest: () -> Unit,
  onConfirmed: (GameCategory) -> Unit,
  onValueChange: (String) -> Unit
)  {
  var selectedGameCategory by remember { mutableStateOf(GameCategory.Undefined) }
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      CenterRow {
        Icon(Icons.Rounded.Construction, null, modifier = Modifier.alpha(0.5f))
        WidthSpacer(value = 6.dp)
        Text(
          text = stringResource(id = R.string.add_new_preset),
          style = AppTheme.typography.headlineMedium
        )
      }
    },
    text = {
      val focusRequester = remember(state) { FocusRequester() }
      val keyboard = LocalSoftwareKeyboardController.current
      Column {
        OutlinedTextField(
          value = state.inputTextManager.text,
          onValueChange = onValueChange,
          label = { Text(stringResource(id = R.string.preset_name)) },
          colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = AppTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.focusRequester(focusRequester),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          isError = state.inputTextManager.isTextError,
          singleLine = true
        )
        AnimatedVisibility(!state.inputTextManager.isTyping) {
          Column {
            if (state.inputTextManager.isTextError) {
              HeightSpacer(value = 10.dp)
              when (state.inputTextManager.error) {
                TextErrorType.NameExisted -> {
                  Text(
                    text = stringResource(id = R.string.preset_name_exists),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.error
                  )
                }
                TextErrorType.LengthLimited -> {
                  Text(
                    text = stringResource(id = R.string.preset_name_too_long),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.error
                  )
                }
                else -> Unit
              }
            }
          }
        }
        HeightSpacer(value = 12.dp)
        CenterRow {
          Icon(Icons.Rounded.SportsEsports, null, modifier = Modifier.alpha(0.5f))
          WidthSpacer(value = 6.dp)
          Text(
            text = stringResource(id = R.string.game_category),
            style = AppTheme.typography.titleMedium
          )
        }
        HeightSpacer(value = 12.dp)
        FlowRow(
          mainAxisSpacing = 20.dp,
          crossAxisSpacing = 6.dp
        ) {
          GameCategory.values().forEach { game ->
            GameCategoryItem(game, selectedGameCategory) { selectedGameCategory = game }
          }
        }
      }
      LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismissRequest
      ) {
        Text(stringResource(id = R.string.Cancel))
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          onConfirmed(selectedGameCategory)
          onDismissRequest()
        },
        enabled = !state.inputTextManager.isTextError && state.inputTextManager.text.isNotEmpty()
      ) {
        Text(stringResource(id = R.string.add))
      }
    }
  )
}
@Composable
fun HelpDialog(
  onDismiss: () -> Unit
) {
  AlertDialog(
    title = {
      Text(
        text = stringResource(id = R.string.Tips),
        style = AppTheme.typography.headlineMedium
      )
    },
    text = {
      Text(
        text = buildAnnotatedString {
          H4Text(text = stringResource(id = R.string.tips1))
          append("\n\n")
          H4Text(text = stringResource(id = R.string.tips2))
          append("\n\n")
          H4Text(text = stringResource(id = R.string.tips3))
        }
      )
    },
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = onDismiss
      ) {
        Text(stringResource(id = R.string.yes))
      }
    }
  )
}

@Composable
fun AnnotatedString.Builder.H4Text(text: String) {
  withStyle(
    style = SpanStyle(fontSize = AppTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Medium)
  ) {
    append(text)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCategoryItem(
  gameCategory: GameCategory,
  selectedGameCategory: GameCategory,
  onSelected: (GameCategory) -> Unit,
) {
  ListItem(
    leadingContent = {
      Image(
        painter = painterResource(id = gameCategory.painter),
        contentDescription = null,
        modifier = Modifier
          .size(35.dp)
          .clip(CircleShape)
      )
    },
    headlineText = { 
      Text(
        text = stringResource(id = gameCategory.gameName)
      )
    },
    trailingContent = {
      RadioButton(
        selected = (selectedGameCategory == gameCategory),
        onClick = { onSelected(gameCategory) }
      )
    },
    modifier = Modifier.selectable(
      selected = selectedGameCategory == gameCategory,
      onClick = { onSelected(gameCategory) },
      role = Role.Tab
    )
  )
}
