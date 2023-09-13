package com.github.whitescent.engine.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.github.whitescent.engine.screen.connection.Connection
import com.github.whitescent.engine.screen.presets.HelpDialog
import com.github.whitescent.engine.screen.presets.NewPresetDialog
import com.github.whitescent.engine.screen.presets.Presets
import com.github.whitescent.engine.screen.settings.Settings
import com.github.whitescent.engine.ui.rememberEngineAppDialogState
import com.github.whitescent.engine.utils.BottomBarItem
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun AppScaffold(
  navigator: DestinationsNavigator
) {
  val pagerState = rememberPagerState()
  val scope = rememberCoroutineScope()
  val dialogState = rememberEngineAppDialogState()
  var selectedScreen by remember { mutableIntStateOf(0) }

  Scaffold(
    bottomBar = {
      BottomBar(
        selectedScreen = selectedScreen,
        onClick = {
          scope.launch {
            pagerState.scrollToPage(it)
          }
        }
      )
    }
  ) {
    HorizontalPager(
      count = BottomBarItem.values().size,
      state = pagerState,
      userScrollEnabled = false,
      modifier = Modifier.padding(bottom = it.calculateBottomPadding())
    ) { page ->
      when (BottomBarItem.values()[page]) {
        BottomBarItem.Connection -> Connection(navigator = navigator)
        BottomBarItem.Presets -> Presets(
          navigator = navigator,
          onFabClick = { dialogState.setShowNewPresetDialog(true) },
          onHelpButtonClick = { dialogState.setShowHelpDialog(true) }
        )
        BottomBarItem.Settings -> Settings(navigator = navigator)
      }
    }
    if (dialogState.shouldShowNewPresetDialog) {
      NewPresetDialog(
        onDismiss = { dialogState.setShowNewPresetDialog(false) }
      )
    }
    if (dialogState.shouldShowHelpDialog) {
      HelpDialog(
        onDismiss = { dialogState.setShowHelpDialog(false) }
      )
    }
  }
  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }.collect { page ->
      selectedScreen = page
    }
  }
}
