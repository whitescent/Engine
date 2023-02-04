package com.github.whitescent.engine.screen.presets

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.whitescent.engine.R
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.destinations.EditorDestination
import com.github.whitescent.engine.ui.component.CenterRow
import com.github.whitescent.engine.ui.component.HeightSpacer
import com.github.whitescent.engine.ui.component.WidthSpacer
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.datetime.*

@Composable
fun Presets(
  viewModel: PresetsViewModel = hiltViewModel(),
  navigator: DestinationsNavigator
) {
  val state by viewModel.uiState.collectAsState()
  val sortPreference by viewModel.sortPreference.collectAsState()

  println("state $state")

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    PresetsTopBar(
      preference = sortPreference,
      onClickSortCategory = viewModel::onClickSortCategory,
      onSortingChanged = viewModel::onSortingChanged
    )
    PresetsList(
      hideDetails = state.hideDetails,
      presetList = viewModel.presetList,
      onClickEditor = {
        navigator.navigate(
          EditorDestination(
            orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            presetModel = it
          )
        )
      },
      deletePresets = viewModel::deletePresets
    )
  }
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomEnd
  ) {
    ExtendedFloatingActionButton(
      onClick = viewModel::onClickFab,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
    ) {
      Icon(Icons.Rounded.Add, null)
      WidthSpacer(value = 4.dp)
      Text(text = stringResource(id = R.string.add_new_preset))
    }
  }
  NewPresetDialog(
    state = state,
    onDismissRequest = viewModel::onDismissRequest,
    onConfirmed = viewModel::onConfirmed,
    onValueChange = viewModel::onValueChange
  )
  LaunchedEffect(Unit) {
    viewModel.getLatestMMKVValue()
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PresetsList(
  hideDetails: Boolean,
  presetList: List<PresetModel>,
  onClickEditor: (PresetModel) -> Unit,
  deletePresets: (PresetModel) -> Unit
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
          modifier = Modifier.fillMaxSize()
        ) {
          items(presetList) { presets ->
            key(presets.createdAt) {
              PresetsListItem(hideDetails, presets, onClickEditor, deletePresets)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsListItem(
  hideDetails: Boolean,
  presetModel: PresetModel,
  onClickEditor: (PresetModel) -> Unit,
  deletePresets: (PresetModel) -> Unit
) {
  val time = Instant.fromEpochMilliseconds(presetModel.createdAt).toLocalDateTime(TimeZone.UTC)
  val name = presetModel.name
  val gameType = presetModel.gameType
  var isExpanded by remember { mutableStateOf(false) }

  val timeString = "${time.date.month.number}-${time.date.dayOfMonth}"

  Column {
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
            WidthSpacer(value = 8.dp)
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
        Image(
          painter = painterResource(id = gameType.painter),
          contentDescription = null,
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
        )
      },
      trailingContent = {
        IconButton(
          onClick = { onClickEditor(presetModel) }
        ) {
          Icon(Icons.Rounded.Edit, null)
        }
      },
      tonalElevation = 2.dp,
      modifier = Modifier.clickable {
        isExpanded = !isExpanded
      }
    )
    AnimatedVisibility(visible = isExpanded) {
      CenterRow(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .background(Color.Green)
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          CenterRow {
            Icon(
              imageVector = Icons.Rounded.Edit,
              contentDescription = null,
              tint = Color.White
            )
            WidthSpacer(value = 6.dp)
            Text(
              text = "修改信息",
              style = AppTheme.typography.bodyMedium,
              color = Color.White
            )
          }
        }
        Box(
          modifier = Modifier
            .weight(1f)
            .background(Color.Red)
            .clickable {
              deletePresets(presetModel)
            }
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          CenterRow {
            Icon(
              imageVector = Icons.Rounded.Delete,
              contentDescription = null,
              tint = Color.White
            )
            WidthSpacer(value = 6.dp)
            Text(
              text = "删除预设",
              style = AppTheme.typography.bodyMedium,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NewPresetDialog(
  state: PresetsUiState,
  onDismissRequest: () -> Unit,
  onConfirmed: (GameCategory) -> Unit,
  onValueChange: (String) -> Unit
) {
  if (state.openDialog) {
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
            value = state.text,
            onValueChange = onValueChange,
            label = { Text(stringResource(id = R.string.preset_name)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
              containerColor = AppTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            isError = state.isTextError,
            singleLine = true
          )
          AnimatedVisibility(state.isTextError) {
            Column {
              HeightSpacer(value = 10.dp)
              Text(
                text = stringResource(id = R.string.preset_name_exists),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colorScheme.error
              )
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
          onClick = { onConfirmed(selectedGameCategory) },
          enabled = !state.isTextError && state.text.isNotEmpty()
        ) {
          Text(stringResource(id = R.string.add))
        }
      }
    )
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
