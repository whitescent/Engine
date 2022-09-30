package com.github.nthily.engine.screen.presets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SportsEsports
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.nthily.engine.AppTheme
import com.github.nthily.engine.R
import com.github.nthily.engine.data.model.PresetsModel
import com.github.nthily.engine.ui.component.CenterRow
import com.github.nthily.engine.ui.component.HeightSpacer
import com.github.nthily.engine.ui.component.WidthSpacer
import com.google.accompanist.flowlayout.FlowRow
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun PresetsRoot(
  viewModel: PresetsViewModel = hiltViewModel()
) {
  val dialogState by viewModel.dialogState.collectAsState()
  val mmkv by viewModel.mmkv.collectAsState()
  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    PresetsTopBar()
    PresetsList(mmkv)
  }
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomEnd
  ) {
    FloatingActionButton(
      onClick = viewModel::onClickFab,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
    ) {
      Icon(Icons.Rounded.Add, null)
    }
  }
  PresetsDialog(
    state = dialogState,
    onDismissRequest = viewModel::onDismissRequest,
    onConfirmed = viewModel::onConfirmed,
    onValueChange = viewModel::onValueChange
  )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PresetsList(
  mmkv: MMKV
) {
  AnimatedContent(mmkv.allKeys()!!.size) {
    when(it) {
      0 -> {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
          CenterRow {
            Icon(Icons.Rounded.Construction, null, modifier = Modifier
              .size(40.dp)
              .alpha(0.5f))
            WidthSpacer(value = 6.dp)
            Text(
              text = stringResource(id = R.string.no_presets_file),
              style = AppTheme.typography.headlineSmall,
              modifier = Modifier.alpha(0.5f)
            )
          }
        }
      }
      else -> {
        val configList = mmkv.allKeys()!!.sortedByDescending { configName ->
          mmkv.decodeParcelable(configName, PresetsModel::class.java)!!.createdAt
        }
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
        ) {
          items(configList) { name ->
            val config = mmkv.decodeParcelable(name, PresetsModel::class.java)
            key(config!!.createdAt) {
              PresetsListItem(name = config.presetsName, createdAt = config.createdAt)
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
  name: String,
  createdAt: Long
) {
  val time = Instant.fromEpochMilliseconds(createdAt).toLocalDateTime(TimeZone.UTC)
  ListItem(
    headlineText = {
     Column {
       Text(
         text = name,
         style = AppTheme.typography.titleMedium,
         color = AppTheme.colorScheme.onSecondaryContainer
       )
       HeightSpacer(value = 4.dp)
     }
    },
    supportingText = {
      Text(
        text = "创建于 ${time.date}",
        style = AppTheme.typography.labelLarge,
        color = AppTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.alpha(0.5f)
      )
    },
    leadingContent = {
//      Icon(Icons.Rounded.Description, null, modifier = Modifier.size(30.dp))
        Image(painterResource(id = R.drawable.assetto_corsa), null, modifier = Modifier.size(30.dp))
    },
    trailingContent = {
      IconButton(
        onClick = { /*TODO*/ }
      ) {
        Icon(Icons.Rounded.Edit, null)
      }
    },
    tonalElevation = 2.dp
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PresetsDialog(
  state: PresetsDialogUiState,
  onDismissRequest: () -> Unit,
  onConfirmed: () -> Unit,
  onValueChange: (String) -> Unit
) {
  if (state.display) {
    AlertDialog(
      onDismissRequest = onDismissRequest,
      title = {
        CenterRow {
          Icon(Icons.Rounded.Construction, null, modifier = Modifier.alpha(0.5f))
          WidthSpacer(value = 6.dp)
          Text(
            text = stringResource(id = R.string.add_new_presets),
            style = AppTheme.typography.headlineMedium
          )
        }
      },
      text = {
        val focusRequester = remember(state) { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        var selectedPainter by remember { mutableStateOf(R.drawable.other_presets) }
        Column {
          OutlinedTextField(
            value = state.text,
            onValueChange = onValueChange,
            label = { Text(stringResource(id = R.string.presets_name)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
              containerColor = AppTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          )
          HeightSpacer(value = 12.dp)
          CenterRow {
            Icon(Icons.Rounded.SportsEsports, null, modifier = Modifier.alpha(0.5f))
            WidthSpacer(value = 6.dp)
            Text(
              text = stringResource(id = R.string.game_type),
              style = AppTheme.typography.titleMedium
            )
          }
          HeightSpacer(value = 12.dp)
          FlowRow(
            mainAxisSpacing = 20.dp,
            crossAxisSpacing = 6.dp
          ) {
            GameItem.values().forEach { game ->
              PresetsTypeIcon(game, selectedPainter) { selectedPainter = game.painter }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsTypeIcon(
  gameItem: GameItem,
  selectedGameItem: Int,
  onSelected: (Int) -> Unit,
) {
  ListItem(
    leadingContent = {
      Image(
        painter = painterResource(id = gameItem.painter),
        contentDescription = null,
        modifier = Modifier
          .size(35.dp)
          .clip(CircleShape)
      )
    },
    headlineText = { 
      Text(
        text = stringResource(id = gameItem.gameName)
      )
    },
    trailingContent = {
      RadioButton(
        selected = (selectedGameItem == gameItem.painter),
        onClick = { onSelected(gameItem.painter) }
      )
    },
    modifier = Modifier.selectable(
      selected = selectedGameItem == gameItem.painter,
      onClick = { onSelected(gameItem.painter) },
      role = Role.Tab
    )
  )
}
