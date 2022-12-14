package com.github.nthily.engine.screen.presets.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nthily.engine.AppTheme
import com.github.nthily.engine.MainActivity
import com.github.nthily.engine.R
import com.github.nthily.engine.data.model.PresetsModel
import com.github.nthily.engine.screen.presets.controller.EngineButton
import com.github.nthily.engine.screen.presets.controller.EngineAxis
import com.github.nthily.engine.screen.presets.controller.AxisOrientation
import com.github.nthily.engine.ui.component.CenterRow
import com.github.nthily.engine.ui.component.EditorDrawer
import com.github.nthily.engine.ui.component.EditorDrawerState
import com.github.nthily.engine.ui.component.WidthSpacer
import com.github.nthily.engine.ui.component.rememberEditorDrawerState
import com.github.nthily.engine.utils.LocalSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

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
  val steeringValue by viewModel.sensorFlow.collectAsStateWithLifecycle()
  val drawerState = rememberEditorDrawerState()
  val scope = rememberCoroutineScope()

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
    presetsModel = presetsModel,
    drawerState = drawerState,
    controllerList = viewModel.controllerList,
    onClickLabel = {
      scope.launch {
        drawerState.open()
      }
    },
    onClickController = {
      viewModel.updateControllerList(it)
      scope.launch {
        drawerState.close()
      }
    }
  )
}

@Composable
fun EditorContent(
  presetsModel: PresetsModel,
  drawerState: EditorDrawerState,
  controllerList: List<ControllerUiModel>,
  onClickLabel: () -> Unit,
  onClickController: (ControllerType) -> Unit
) {
  EditorDrawer(
    drawerContent = { EditorDrawerContent(onClickController) },
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
        onClickLabel = onClickLabel
      )
      controllerList.forEach {

        var selected by remember { mutableStateOf(UUID.randomUUID()) }

        when (it.controllerType) {
          ControllerType.Axis -> {
            EngineAxis(
              onValueChanged = { },
              modifier = Modifier
                .size(60.dp, 120.dp)
                .offset { IntOffset(it.offsetX, it.offsetY) }
            )
          }
          ControllerType.RectangularButton -> {
            var scale by remember { mutableStateOf(1f) }
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }
            val state = rememberTransformableState { zoomChange, _, _ ->
              scale *= zoomChange
            }
            val statusBorderModifier = Modifier
              .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
              .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                  change.consume()
                  offsetX += dragAmount.x
                  offsetY += dragAmount.y
                }
              }
              .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
              )
              .transformable(state = state)
              .border(10.dp, Color.Gray)
            EngineButton(
              modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .then(if (selected == it.uuid) statusBorderModifier else Modifier),
              shape = RectangleShape
            ) {
              selected = it.uuid
            }
          }
          ControllerType.RoundButton -> {
            EngineButton(
              modifier = Modifier
                .size(100.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun EditorDrawerContent(
  onClickController: (ControllerType) -> Unit
) {
  Box(
    modifier = Modifier
      .width(250.dp)
      .background(
        AppTheme.colorScheme.secondaryContainer,
        RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)
      )
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
    ) {
      item {
        CenterRow(modifier = Modifier.padding(18.dp)) {
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
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickController(ControllerType.RoundButton) }
        ) {
          CenterRow(Modifier.padding(14.dp)) {
            EngineButton(
              modifier = Modifier.size(65.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
              text = "圆形按钮",
              style = AppTheme.typography.titleLarge,
              color = AppTheme.colorScheme.onSecondaryContainer
            )
          }
        }
      }
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickController(ControllerType.RectangularButton) }
        ) {
          CenterRow(Modifier.padding(14.dp)) {
            EngineButton(
              modifier = Modifier.size(65.dp),
              shape = RectangleShape
            )
            Spacer(Modifier.weight(1f))
            Text(
              text = "矩形按钮",
              style = AppTheme.typography.titleLarge,
              color = AppTheme.colorScheme.onSecondaryContainer
            )
          }
        }
      }
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClickController(ControllerType.Axis) },
          contentAlignment = Alignment.Center
        ) {
          CenterRow(Modifier.padding(horizontal = 14.dp)) {
            EngineAxis(
              onValueChanged = {

              },
              modifier = Modifier
                .width(50.dp)
                .height(150.dp),
              orientation = AxisOrientation.Horizontal
            )
            Spacer(Modifier.weight(1f))
            Text(
              text = "轴",
              style = AppTheme.typography.titleLarge,
              color = AppTheme.colorScheme.onSecondaryContainer
            )
          }
        }
      }
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
