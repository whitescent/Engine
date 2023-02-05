package ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import utils.LocalMyWindowState

@Composable
fun WindowScope.AppTopBar() = WindowDraggableArea {
  val state = LocalMyWindowState.current
  CenterRow(
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .background(Color.DarkGray)
      .padding(horizontal = 8.dp)
  ) {
    Text(
      text = "EngineServer",
      fontSize = 16.sp,
      color = Color.White,
      fontFamily = LocalTextStyle.current.fontFamily
    )
    Spacer(Modifier.weight(1f))
    IconButton(
      onClick = {
        state.isMinimized = true
      }
    ) {
      Icon(Icons.Rounded.Minimize, null, tint = Color.White)
    }
    IconButton(
      onClick = {
        state.isClosed = true
      }
    ) {
      Icon(Icons.Rounded.Close, null, tint = Color.White)
    }
  }
}
