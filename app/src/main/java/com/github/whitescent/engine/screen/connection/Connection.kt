package com.github.whitescent.engine.screen.connection

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.destinations.ConsoleDestination
import com.github.whitescent.engine.ui.component.CenterRow
import com.github.whitescent.engine.ui.component.HeightSpacer
import com.github.whitescent.engine.ui.component.WidthSpacer
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Connection(
  viewModel: ConnectionViewModel = hiltViewModel(),
  navigator: DestinationsNavigator
) {
  val state by viewModel.uiState.collectAsState()
  val snackState = remember { SnackbarHostState() }
  AnimatedContent(state.selectedPreset) {
    when (it) {
      null -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "需要有至少一个预设才能连接主机",
            style = AppTheme.typography.titleLarge,
            color = Color.Gray
          )
        }
      }
      else -> {
        Column(
          modifier = Modifier.fillMaxSize()
        ) {
          ConnectionTopBar()
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            ConnectionPanel(
              state = state,
              updatePresets = viewModel::updateSelectedPreset,
              updateHostName = viewModel::updateHostName
            )
            ExtendedFloatingActionButton(
              onClick = {
                navigator.navigate(
                  ConsoleDestination(
                    orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
                    presetsModel = state.selectedPreset!!
                  )
                )
              },
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
            ) {
              Icon(Icons.Rounded.Link, null)
              WidthSpacer(value = 4.dp)
              Text(text = "连接")
            }
          }
        }
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.BottomCenter
        ) {
          SnackbarHost(hostState = snackState)
        }
      }
    }
  }
  DisposableEffect(Unit) {
    viewModel.initUiState()
    onDispose {
      viewModel.resetUiState()
    }
  }
  LaunchedEffect(state.errorMessage) {
    state.errorMessage?.let {
      snackState.showSnackbar(it, actionLabel = "确认")
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionPanel(
  state: ConnectionUiState,
  updatePresets: (PresetsModel) -> Unit,
  updateHostName: (String) -> Unit
) {

  var showPresets by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 25.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    OutlinedTextField(
      value = state.hostname,
      onValueChange = { updateHostName(it) },
      label = {
        Text(text = "主机名")
      },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )
    HeightSpacer(value = 16.dp)
    state.selectedPreset?.let { selectedPreset ->
      Column {
        val shape = if (showPresets)
          RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        else RoundedCornerShape(8.dp)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = AppTheme.colorScheme.secondaryContainer)
            .clickable {
              showPresets = !showPresets
            }
        ) {
          CenterRow(
            modifier = Modifier
              .padding(12.dp)
          ) {
            Image(
              painter = painterResource(id = selectedPreset.gameType.painter),
              contentDescription = null,
              modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
            )
            WidthSpacer(value = 6.dp)
            Text(
              text = selectedPreset.presetsName,
              style = AppTheme.typography.titleSmall,
              color = AppTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ExpandMore, null)
          }
        }
        AnimatedVisibility(visible = showPresets) {
          Column {
            Divider(thickness = 0.5.dp)
            PresetsSelector(
              list = state.presets,
              updatePresets = {
                updatePresets(it)
                showPresets = false
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun PresetsSelector(
  list: List<PresetsModel>,
  updatePresets: (PresetsModel) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .height(150.dp)
      .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
      .background(color = AppTheme.colorScheme.primaryContainer)
  ) {
    items(list) { presets ->
      Column {
        SelectorListItem(
          presets = presets,
          onClick = { updatePresets(it) }
        )
        Divider(thickness = 0.5.dp)
      }
    }
  }
}
@Composable
fun SelectorListItem(
  presets: PresetsModel,
  onClick: (PresetsModel) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        onClick(presets)
      }
  ) {
    CenterRow(
      modifier = Modifier.padding(12.dp)
    ) {
      Image(
        painter = painterResource(id = presets.gameType.painter),
        contentDescription = null,
        modifier = Modifier
          .size(25.dp)
          .clip(CircleShape)
      )
      WidthSpacer(value = 6.dp)
      Text(
        text = presets.presetsName,
        style = AppTheme.typography.titleSmall,
        color = AppTheme.colorScheme.onSecondaryContainer
      )
    }
  }
}
