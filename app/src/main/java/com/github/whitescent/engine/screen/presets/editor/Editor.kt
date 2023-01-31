package com.github.whitescent.engine.screen.presets.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
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
import com.github.whitescent.engine.data.model.Position
import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.data.model.WidgetModel
import com.github.whitescent.engine.data.model.WidgetType
import com.github.whitescent.engine.screen.presets.widget.EngineButton
import com.github.whitescent.engine.screen.presets.widget.EngineAxis
import com.github.whitescent.engine.screen.presets.widget.AxisOrientation
import com.github.whitescent.engine.ui.component.*
import com.github.whitescent.engine.utils.LocalSystemUiController
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun Editor(
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
    viewModel.readWidgetList(presetsModel.presetsName)
    onDispose {
      activity.requestedOrientation = originalOrientation
      systemUiController.isSystemBarsVisible = true
      viewModel.stopListeningSensor() // stop listening sensor.
      viewModel.saveWidgetList(presetsModel.presetsName)
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
    addWidget = {
      viewModel.addNewWidget(it)
      scope.launch {
        drawerState.close()
      }
    },
    updateWidgetPos = { index, position ->
      viewModel.updateWidgetPos(index, position)
    },
    deleteWidget = viewModel::deleteWidget
  )
}

@Composable
fun EditorContent(
  presetsModel: PresetsModel,
  drawerState: EditorDrawerState,
  widgetList: List<WidgetModel>,
  onClickLabel: () -> Unit,
  addWidget: (WidgetType) -> Unit,
  updateWidgetPos: (Int, Position) -> Unit,
  deleteWidget: (WidgetModel) -> Unit
) {
  EditorDrawer(
    drawerContent = { EditorDrawerContent(addWidget) },
    drawerState = drawerState
  ) {
    var selected by remember { mutableStateOf(0) }
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
      if (widgetList.isNotEmpty()) {
        widgetList.forEachIndexed { index, it ->
          var scale by remember { mutableStateOf(it.position.scale) }
          var offsetX by remember { mutableStateOf(it.position.offsetX) }
          var offsetY by remember { mutableStateOf(it.position.offsetY) }
          var openDialog by remember { mutableStateOf(false) }
          val selectedModifier = Modifier
            .graphicsLayer(
              scaleX = it.position.scale,
              scaleY = it.position.scale,
              translationX = it.position.offsetX * it.position.scale,
              translationY = it.position.offsetY * it.position.scale
            )
            .pointerInput(Unit) {
              if (it.widgetType == WidgetType.Axis) {
                detectTapGestures(
                  onDoubleTap = {
                    openDialog = true
                  }
                )
              }
            }
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
                index -> {
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
                    onPress = { selected = index }
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
                index -> {
                  EngineButton(
                    modifier = Modifier
                      .size(160.dp)
                      .align(Alignment.Center)
                      .then(selectedModifier)
                      .onEditingBorder(),
                    shape = shape,
                    onDoubleClick = { openDialog = true }
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
                    selected = index
                  }
                }
              }
            }
          }
          if (openDialog) {
            DeleteDialog(
              onDismissRequest = { openDialog = false },
              onConfirmed = {
                deleteWidget(it)
                openDialog = false
              }
            )
          }
        }
      } else {
        Text(
          text = stringResource(id = R.string.null_widget),
          modifier = Modifier
            .align(Alignment.Center)
            .alpha(0.5f),
          color = AppTheme.colorScheme.onBackground
        )
      }
    }
  }
}

@Composable
fun DeleteDialog(
  onDismissRequest: () -> Unit,
  onConfirmed: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      CenterRow {
        Icon(Icons.Rounded.Construction, null, modifier = Modifier.alpha(0.5f))
        WidthSpacer(value = 6.dp)
        Text(
          text = "删除组件",
          style = AppTheme.typography.headlineMedium
        )
      }
    },
    text = {
      Text(
        text = "你确认删除这个组件吗？",
        style = AppTheme.typography.headlineMedium
      )
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
        onClick = { onConfirmed() }
      ) {
        Text("确认")
      }
    }
  )
}

@Composable
fun EditorDrawerContent(
  addWidget: (WidgetType) -> Unit
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
        Column(
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = stringResource(id = R.string.buttons),
            style = AppTheme.typography.titleLarge,
            color = Color.Gray,
            modifier = Modifier.padding(10.dp)
          )
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 25.dp,
            mainAxisAlignment = FlowMainAxisAlignment.Center
          ) {
            EngineButton(
              modifier = Modifier
                .size(65.dp)
            ) {
              addWidget(WidgetType.RoundButton)
            }
            EngineButton(
              modifier = Modifier
                .size(65.dp),
              shape = RectangleShape
            ) {
              addWidget(WidgetType.RectangularButton)
            }
          }
        }
      }
      item {
        Column {
          Text(
            text = stringResource(id = R.string.axis),
            style = AppTheme.typography.titleLarge,
            color = Color.Gray,
            modifier = Modifier.padding(10.dp)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
              .clickable { addWidget(WidgetType.Axis) },
            contentAlignment = Alignment.Center
          ) {
            EngineAxis(
              onValueChanged = {

              },
              modifier = Modifier
                .width(50.dp)
                .height(150.dp),
              orientation = AxisOrientation.Horizontal
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