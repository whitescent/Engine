package utils

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState

class MyWindowStateImpl(
  placement: WindowPlacement,
  isMinimized: Boolean,
  isClosed: Boolean,
  position: WindowPosition,
  size: DpSize
) : MyWindowState {

  override var placement by mutableStateOf(placement)
  override var isMinimized by mutableStateOf(isMinimized)
  override var position by mutableStateOf(position)
  override var size by mutableStateOf(size)
  override var isClosed by mutableStateOf(isClosed)

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
        MyWindowStateImpl(
          placement = WindowPlacement.values()[state[0] as Int],
          isMinimized = state[1] as Boolean,
          isClosed = state[2] as Boolean,
          position = if (state[3] as Boolean) {
            WindowPosition((state[4] as Float).dp, (state[5] as Float).dp)
          } else {
            unspecifiedPosition
          },
          size = DpSize((state[6] as Float).dp, (state[7] as Float).dp),
        )
      }
    )
  }
}

interface MyWindowState: WindowState {
  override var placement: WindowPlacement
  override var isMinimized: Boolean
  override var position: WindowPosition
  override var size: DpSize
  var isClosed: Boolean
}

@Composable
fun rememberMyWindowState(
  placement: WindowPlacement = WindowPlacement.Floating,
  isMinimized: Boolean = false,
  isClosed: Boolean = false,
  position: WindowPosition = WindowPosition.PlatformDefault,
  size: DpSize = DpSize(470.dp, 280.dp),
): MyWindowState = rememberSaveable(saver = MyWindowStateImpl.Saver(position)) {
  MyWindowStateImpl(
    placement,
    isMinimized,
    isClosed,
    position,
    DpSize(size.width, size.height)
  )
}

val LocalMyWindowState = compositionLocalOf<MyWindowState> {
  error("CompositionLocal LocalWindowState not present")
}
