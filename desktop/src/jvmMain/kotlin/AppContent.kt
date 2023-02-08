import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import network.SocketUiState
import ui.component.CenterRow
import ui.component.WidthSpacer
import utils.Vjoy

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppContent(
  vjoy: Vjoy,
  socketUiState: SocketUiState
) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    when (vjoy.vJoyEnabled) {
      true -> {
        val centerRowItemSpace = 25.dp
        Column(
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          CenterRow(
            horizontalArrangement = Arrangement.spacedBy(centerRowItemSpace)
          ) {
            Icon(Icons.Rounded.Handyman, null, tint = Color(0xFF0079D3))
            Text(
              text = "vJoy Enabled"
            )
          }
          CenterRow(
            horizontalArrangement = Arrangement.spacedBy(centerRowItemSpace)
          ) {
            Icon(Icons.Rounded.Label, null, tint = Color(0xFF0079D3))
            Text(
              text = "vJoy ver.${vjoy.vJoyVersion}"
            )
          }
          CenterRow(
            horizontalArrangement = Arrangement.spacedBy(centerRowItemSpace)
          ) {
            Icon(Icons.Rounded.Link, null, tint = Color(0xFF0079D3))
            Text(
              text = "Local server is up and running"
            )
          }
          AnimatedContent(socketUiState.receivedData) {
            when (it) {
              true -> {
                CenterRow(
                  horizontalArrangement = Arrangement.spacedBy(centerRowItemSpace)
                ) {
                  Icon(Icons.Rounded.Done, null, tint = Color(0xFF0079D3))
                  Text(
                    text = "Receiving data..."
                  )
                }
              }
              false -> {
                CenterRow(
                  horizontalArrangement = Arrangement.spacedBy(centerRowItemSpace)
                ) {
                  Icon(Icons.Rounded.Close, null, tint = Color(0xFF0079D3))
                  Text(
                    text = "No data received"
                  )
                }
              }
            }
          }
        }
      }

      else -> {
        CenterRow {
          Icon(Icons.Rounded.SentimentDissatisfied, null)
          WidthSpacer(6.dp)
          Text(
            text = "vJoy driver not detected."
          )
        }
      }
    }
  }
}