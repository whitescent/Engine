package com.github.whitescent.engine.screen.connection.console

import android.view.KeyEvent.*
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.MainActivity
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.WidgetType
import com.github.whitescent.engine.screen.presets.editor.widget.EngineAxis
import com.github.whitescent.engine.screen.presets.editor.widget.EngineButton
import com.github.whitescent.engine.utils.LocalSystemUiController
import com.github.whitescent.engine.utils.vibrate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun Console(
  orientation: Int,
  presetModel: PresetModel,
  viewModel: ConsoleViewModel = hiltViewModel(),
  navigator: DestinationsNavigator
) {
  val requester = remember { FocusRequester() }
  val context = LocalContext.current
  val activity = context as MainActivity
  val systemUiController = LocalSystemUiController.current

  val sensor by viewModel.sensorFlow.collectAsStateWithLifecycle()
  val state by viewModel.consoleUiState.collectAsState()

  val widgetList = presetModel.widgetList
  val axisList = widgetList.filter { it.widgetType == WidgetType.Axis }
  val buttonList = widgetList.filter {
    it.widgetType == WidgetType.RectangularButton || it.widgetType == WidgetType.RoundButton
  }

  systemUiController.systemBarsBehavior =
    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

  DisposableEffect(Unit) {
    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = orientation
    systemUiController.isSystemBarsVisible = false
    viewModel.startListeningSensor()
    viewModel.initButtons(buttonList.size)
    requester.requestFocus()
    onDispose {
      activity.requestedOrientation = originalOrientation
      systemUiController.isSystemBarsVisible = true
      viewModel.stopListeningSensor()
      viewModel.resetButtons()
    }
  }

  LaunchedEffect(state.error) {
    if (state.error) {
      navigator.popBackStack()
    }
  }

  val enableVolumeButton = if (state.volumeButtonEnabled) {
    Modifier.onKeyEvent {
      when (it.type) {
        KeyEventType.KeyUp -> { // onRelease
          when (it.nativeKeyEvent.keyCode) {
            KEYCODE_VOLUME_DOWN -> viewModel.updateButton(1, 0)
            KEYCODE_VOLUME_UP -> viewModel.updateButton(0, 0)
          }
        }
        KeyEventType.KeyDown -> { // onPress
          when (it.nativeKeyEvent.keyCode) {
            KEYCODE_VOLUME_DOWN -> viewModel.updateButton(1, 1)
            KEYCODE_VOLUME_UP -> viewModel.updateButton(0, 1)
            KEYCODE_BACK -> navigator.popBackStack()
          }
        }
      }
      true
    }
  } else Modifier

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(AppTheme.colorScheme.background)
      .focusRequester(requester)
      .focusable()
      .then(enableVolumeButton)
  ) {
    axisList.forEachIndexed { index, it ->
      EngineAxis(
        onValueChanged = {
          when (index) {
            0 -> viewModel.updateAxisY(it)
            1 -> viewModel.updateAxisZ(it)
            2 -> viewModel.updateAxisRx(it)
            3 -> viewModel.updateAxisRy(it)
            4 -> viewModel.updateAxisRz(it)
            5 -> viewModel.updateAxisSl0(it)
            6 -> viewModel.updateAxisSl1(it)
          }
        },
        modifier = Modifier
          .size(100.dp, 200.dp)
          .align(Alignment.Center)
          .graphicsLayer(
            scaleX = it.position.scale,
            scaleY = it.position.scale,
            translationX = it.position.offsetX * it.position.scale,
            translationY = it.position.offsetY * it.position.scale
          )
      )
    }
    buttonList.forEachIndexed { index, it ->
      val shape = when(it.widgetType) {
        WidgetType.RectangularButton -> RectangleShape
        else -> CircleShape
      }
      EngineButton(
        modifier = Modifier
          .size(160.dp)
          .align(Alignment.Center)
          .graphicsLayer(
            scaleX = it.position.scale,
            scaleY = it.position.scale,
            translationX = it.position.offsetX * it.position.scale,
            translationY = it.position.offsetY * it.position.scale
          ),
        shape = shape,
        onPress = {
          if (state.buttonVibration) context.vibrate(VIBRATE_TIME)
          when (state.volumeButtonEnabled) {
            true -> viewModel.updateButton((index + 2).toShort(), 1)
            false -> viewModel.updateButton(index.toShort(), 1)
          }
        },
        onTap = {
          if (state.buttonVibration) context.vibrate(VIBRATE_TIME)
          when (state.volumeButtonEnabled) {
            true -> viewModel.updateButton((index + 2).toShort(), 0)
            false -> viewModel.updateButton(index.toShort(), 0)
          }
        }
      )
    }
  }

  LaunchedEffect(sensor) {
    viewModel.updateAxisX(sensor)
  }

}

private const val VIBRATE_TIME: Long = 20
