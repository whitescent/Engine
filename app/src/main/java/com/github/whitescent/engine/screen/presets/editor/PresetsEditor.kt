package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.MainActivity
import com.github.whitescent.engine.R
import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.screen.presets.widget.EngineButton
import com.github.whitescent.engine.screen.presets.widget.EngineAxis
import com.github.whitescent.engine.screen.presets.widget.AxisOrientation
import com.github.whitescent.engine.ui.component.CenterRow
import com.github.whitescent.engine.ui.component.EditorDrawer
import com.github.whitescent.engine.ui.component.EditorDrawerState
import com.github.whitescent.engine.ui.component.WidthSpacer
import com.github.whitescent.engine.ui.component.rememberEditorDrawerState
import com.github.whitescent.engine.utils.LocalSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import java.util.UUID

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
    widgetList = viewModel.widgetList,
    onClickLabel = {
      scope.launch {
        drawerState.open()
      }
    },
    onSelectWidget = {
      viewModel.addNewWidget(it)
      scope.launch {
        drawerState.close()
      }
    },
    updateWidgetPos = { index, position ->
      viewModel.updateControllerPos(index, position)
    }
  )
}

@Composable
fun EditorContent(
  presetsModel: PresetsModel,
  drawerState: EditorDrawerState,
  widgetList: List<WidgetUiModel>,
  onClickLabel: () -> Unit,
  onSelectWidget: (WidgetType) -> Unit,
  updateWidgetPos: (Int, Position) -> Unit
) {
  EditorDrawer(
    drawerContent = { EditorDrawerContent(onSelectWidget) },
    drawerState = drawerState
  ) {
    var selected by remember { mutableStateOf(UUID.randomUUID()) }
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
      widgetList.forEachIndexed { index, it ->
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        val selectedModifier = Modifier
          .graphicsLayer(
            scaleX = it.position.scale,
            scaleY = it.position.scale,
            translationX = it.position.offsetX * it.position.scale,
            translationY = it.position.offsetY * it.position.scale
          )
          .pointerInput(Unit) {
            detectTransformGestures(
              onGesture = { _, pan, zoom, _ ->
                offsetX += pan.x
                offsetY += pan.y
                scale *= zoom
                updateWidgetPos(
                  index,
                  it.position.copy(
                    offsetX = offsetX,
                    offsetY = offsetY,
                    scale = scale
                  )
                )
              }
            )
          }
        when (it.widgetType) {
          WidgetType.Axis -> {
            when (selected) {
              it.uuid -> {
                EngineAxis(
                  onValueChanged = { },
                  modifier = Modifier
                    .size(60.dp, 120.dp)
                    .align(Alignment.Center)
                    .then(selectedModifier)
                    .onEditingBorder(),
                  enabledScroll = false
                )
              }
              else -> {
                EngineAxis(
                  onValueChanged = { },
                  modifier = Modifier
                    .size(60.dp, 120.dp)
                    .align(Alignment.Center)
                    .graphicsLayer(
                      scaleX = it.position.scale,
                      scaleY = it.position.scale,
                      translationX = it.position.offsetX * it.position.scale,
                      translationY = it.position.offsetY * it.position.scale
                    ),
                  onPress = { selected = it.uuid }
                )
              }
            }
          }
          else -> {
            val shape = when(it.widgetType) {
              WidgetType.RectangularButton -> RectangleShape
              else -> CircleShape
            }
            when (selected) {
              it.uuid -> {
                EngineButton(
                  modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.Center)
                    .then(selectedModifier)
                    .onEditingBorder(),
                  shape = shape
                )
              }
              else -> {
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
                  shape = shape
                ) {
                  selected = it.uuid
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun EditorDrawerContent(
  onClickWidget: (WidgetType) -> Unit
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
            text = stringResource(id = R.string.Add_widget),
            style = AppTheme.typography.headlineMedium,
            color = AppTheme.colorScheme.onSecondaryContainer
          )
        }
      }
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickWidget(WidgetType.RoundButton) }
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
            .clickable { onClickWidget(WidgetType.RectangularButton) }
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
            .clickable { onClickWidget(WidgetType.Axis) },
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
