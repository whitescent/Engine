package com.github.whitescent.engine.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.whitescent.engine.AppTheme
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun AboutLibrary() {
  Column(Modifier.fillMaxSize().background(AppTheme.colorScheme.background).statusBarsPadding()) {
    LibrariesContainer(
      modifier = Modifier.fillMaxSize(),
      colors = LibraryDefaults.libraryColors(
        backgroundColor = AppTheme.colorScheme.background,
        contentColor = AppTheme.colorScheme.onBackground
      )
    )
  }
}
