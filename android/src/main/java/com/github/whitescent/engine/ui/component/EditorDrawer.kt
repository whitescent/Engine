package com.github.whitescent.engine.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableDefaults.AnimationSpec
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch

enum class EditorDrawerValue {
  Closed, Open
}

@OptIn(ExperimentalMaterialApi::class)
@Stable
class EditorDrawerState(
  initialValue: EditorDrawerValue,
  confirmStateChange: (EditorDrawerValue) -> Boolean = { true }
) {
  val swipeableState = SwipeableState(
    initialValue = initialValue,
    animationSpec = AnimationSpec,
    confirmStateChange = confirmStateChange
  )
  
  private val currentValue: EditorDrawerValue
    get() = swipeableState.currentValue
  
  val isOpen
    get() = currentValue == EditorDrawerValue.Open
  
  val offset: State<Float>
    get() = swipeableState.offset
  
  suspend fun open() = animateTo(EditorDrawerValue.Open, AnimationSpec)
  
  suspend fun close() = animateTo(EditorDrawerValue.Closed, AnimationSpec)
  
  private suspend fun animateTo(targetValue: EditorDrawerValue, anim: AnimationSpec<Float>) {
    swipeableState.animateTo(targetValue, anim)
  }
  
  
}

@Composable
fun rememberEditorDrawerState(
  initialValue: EditorDrawerValue = EditorDrawerValue.Closed
) = remember(initialValue) {
  EditorDrawerState(initialValue)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditorDrawer(
  drawerContent: @Composable () -> Unit,
  drawerState: EditorDrawerState = rememberEditorDrawerState(),
  content: @Composable () -> Unit
) {
  val scope = rememberCoroutineScope()
  BoxWithConstraints {
    val minValue = maxWidth.value
    val maxValue = 0f
    val anchors = mapOf(
      minValue to EditorDrawerValue.Closed,
      maxValue to EditorDrawerValue.Open
    )
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val fraction = calculateFraction(minValue, maxValue, drawerState.offset.value)
    
    Box(
      modifier = Modifier
        .fillMaxSize()
        .swipeable(
          state = drawerState.swipeableState,
          anchors = anchors,
          thresholds = { _, _ -> FractionalThreshold(0.6f) },
          orientation = Orientation.Horizontal,
          reverseDirection = isRtl,
          enabled = false
        )
    ) {
      Layout(
        content = {
          Box {
            content()
          }
          Scrim(
            open = drawerState.isOpen,
            onClose = {
              scope.launch { drawerState.close() }
            },
            fraction = { fraction },
            color = Color.Black.copy(alpha = 0.32f)
          )
          Box {
            drawerContent()
          }
        }
      ) { measurables, constraints ->
        val contentPlaceable = measurables[0].measure(constraints)
        val scrimPlaceable = measurables[1].measure(constraints)
        val drawerPlaceable = measurables[2].measure(constraints)
        
        layout(contentPlaceable.width, contentPlaceable.height) {
          contentPlaceable.placeRelative(0, 0)
          scrimPlaceable.placeRelative(0, 0)
          
          val drawerWidth = drawerPlaceable.width
          val drawerOffset = lerp(
            start = contentPlaceable.width,
            stop = contentPlaceable.width - drawerWidth,
            fraction = fraction
          )
          drawerPlaceable.placeRelative(drawerOffset, 0)
        }
      }
    }
  }
}

@Composable
private fun Scrim(
  open: Boolean,
  onClose: () -> Unit,
  fraction: () -> Float,
  color: Color
) {
  val dismissDrawer = if (open) {
    Modifier.pointerInput(onClose) { detectTapGestures { onClose() } }
  } else {
    Modifier
  }
  Canvas(
    Modifier
      .fillMaxSize()
      .then(dismissDrawer)
  ) {
    drawRect(color, alpha = fraction())
  }
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
  ((pos - a) / (b - a)).coerceIn(0f, 1f)
