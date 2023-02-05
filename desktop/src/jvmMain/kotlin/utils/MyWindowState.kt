package utils

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState

class MyWindowState(
  override var isMinimized: Boolean,
  override var placement: WindowPlacement,
  override var position: WindowPosition,
  override var size: DpSize
) : WindowState {
  var isClosed by mutableStateOf(false)
  companion object {
    fun Saver(unspecifiedPosition: WindowPosition) = listSaver<MyWindowState, Any>(
      save = {
        listOf(
          it.placement.ordinal,
          it.isMinimized,
          it.position.isSpecified,
          it.position.x.value,
          it.position.y.value,
          it.size.width.value,
          it.size.height.value,
        )
      },
      restore = { state ->
        MyWindowState(
          placement = WindowPlacement.values()[state[0] as Int],
          isMinimized = state[1] as Boolean,
          position = if (state[2] as Boolean) {
            WindowPosition((state[3] as Float).dp, (state[4] as Float).dp)
          } else {
            unspecifiedPosition
          },
          size = DpSize((state[5] as Float).dp, (state[6] as Float).dp),
        )
      }
    )
  }
}

@Composable
fun rememberMyWindowState(
  placement: WindowPlacement = WindowPlacement.Floating,
  isMinimized: Boolean = false,
  position: WindowPosition = WindowPosition.PlatformDefault,
  size: DpSize = DpSize(470.dp, 280.dp),
): MyWindowState = rememberSaveable(saver = MyWindowState.Saver(position)) {
  MyWindowState(
    isMinimized,
    placement,
    position,
    size
  )
}

val LocalMyWindowState = compositionLocalOf<MyWindowState> {
  error("CompositionLocal LocalWindowState not present")
}
