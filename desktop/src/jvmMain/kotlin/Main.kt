import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import network.MySocket
import ui.component.AppTopBar
import utils.LocalMyWindowState
import utils.Vjoy
import utils.rememberMyWindowState
fun main() = application {
  val state = rememberMyWindowState()
  if (!state.isClosed) {
    Window(
      onCloseRequest = { state.isClosed = true },
      state = state,
      resizable = false,
      undecorated = true,
      transparent = true
    ) {
      CompositionLocalProvider(LocalMyWindowState provides state) {
        App()
      }
    }
  }
}

@Composable
@Preview
fun WindowScope.App() {
  val vjoy = Vjoy()
  val socket = MySocket()
  val socketUiState by socket.socketUiState.collectAsState()
  MaterialTheme {
    CompositionLocalProvider(
      LocalTextStyle provides TextStyle(
        fontFamily = FontFamily(
          Font(
            resource = "fonts/PingFang Bold.ttf",
            style = FontStyle.Normal
          )
        )
      )
    ) {
      Surface(
        modifier = Modifier.fillMaxSize().padding(5.dp).shadow(3.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Column {
          AppTopBar()
          AppContent(vjoy, socketUiState)
        }
      }
    }
  }
  LaunchedEffect(Unit) {
    socket.init(vjoy)
  }
}

typealias AppTheme = MaterialTheme
