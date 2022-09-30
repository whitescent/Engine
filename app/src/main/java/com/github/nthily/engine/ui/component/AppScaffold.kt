package com.github.nthily.engine.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.github.nthily.engine.screen.presets.PresetsRoot
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
  val pagerState = rememberPagerState()
  val scope = rememberCoroutineScope()
  var selectedScreen by remember { mutableStateOf(0) }

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
      when(BottomBarItem.values()[page]) {
        BottomBarItem.Connect -> Unit
        BottomBarItem.Config -> PresetsRoot()
        BottomBarItem.Settings -> Unit
      }
    }
  }
  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }.collect { page ->
      selectedScreen = page
    }
  }
}
