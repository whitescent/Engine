package com.github.nthily.engine.screen.presets.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nthily.engine.AppTheme
import com.github.nthily.engine.MainActivity
import com.github.nthily.engine.R
import com.github.nthily.engine.data.model.PresetsModel
import com.github.nthily.engine.ui.component.CenterRow
import com.github.nthily.engine.ui.component.EditorDrawer
import com.github.nthily.engine.ui.component.HeightSpacer
import com.github.nthily.engine.ui.component.WidthSpacer
import com.github.nthily.engine.ui.component.rememberEditorDrawerState
import com.github.nthily.engine.utils.LocalSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun PresetsEditor(
  orientation: Int,
  presetsModel: PresetsModel,
  viewModel: EditorViewModel = hiltViewModel()
) {
  val activity = LocalContext.current as MainActivity
  val systemUiController = LocalSystemUiController.current
  val dialogState by viewModel.dialogState.collectAsState()
  val steeringValue by viewModel.sensorFlow.collectAsStateWithLifecycle()

  systemUiController.systemBarsBehavior =
    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
  DisposableEffect(Unit) {

    // Force this @Composable to be landscape and hide the statusBar.
    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = orientation
    systemUiController.isSystemBarsVisible = false
    viewModel.startListeningSensor()
    onDispose {
      activity.requestedOrientation = originalOrientation
      systemUiController.isSystemBarsVisible = true
      viewModel.stopListeningSensor() // stop listening sensor.
    }
  }

  EditorContent(
    presetsModel,
    dialogState,
    viewModel::onClickLabel,
    viewModel::onDismissRequest
  )
}

@Composable
fun EditorContent(
  presetsModel: PresetsModel,
  dialogState: Boolean,
  onClickLabel: () -> Unit,
  onDismissRequest: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val drawerState = rememberEditorDrawerState()
  
  EditorDrawer(
    drawerContent = {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .background(
            AppTheme.colorScheme.secondaryContainer,
            RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)
          )
      ) {
        LazyColumn(
          modifier = Modifier
            .padding(12.dp)
        ) {
          item {
            CenterRow {
              Icon(
                imageVector = Icons.Rounded.Construction,
                contentDescription = null,
                modifier = Modifier.alpha(0.5f),
                tint = AppTheme.colorScheme.onBackground
              )
              WidthSpacer(value = 6.dp)
              Text(
                text = stringResource(id = R.string.choose_controller),
                style = AppTheme.typography.headlineMedium,
                color = AppTheme.colorScheme.onSecondaryContainer
              )
            }
          }
        }
      }
    },
    drawerState = drawerState
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colorScheme.background)
    ) {
      PresetsLabel(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(10.dp),
        presetsModel = presetsModel,
        onClickLabel = {
          scope.launch {
            drawerState.open()
          }
        }
      )
    }
  }
}

@Composable
fun PresetsLabel(
  modifier: Modifier = Modifier,
  presetsModel: PresetsModel,
  onClickLabel: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClickLabel)
      .background(AppTheme.colorScheme.secondaryContainer)
  ) {
    CenterRow(Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
      Image(
        painter = painterResource(id = presetsModel.gameType.painter),
        contentDescription = null,
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
      )
      WidthSpacer(value = 6.dp)
      Text(
        text = presetsModel.presetsName,
        style = AppTheme.typography.labelMedium,
        modifier = Modifier.alpha(0.5f),
        color = AppTheme.colorScheme.onSecondaryContainer
      )
    }
  }
}
