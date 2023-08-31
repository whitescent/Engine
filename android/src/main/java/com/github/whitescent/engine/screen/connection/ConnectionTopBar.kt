package com.github.whitescent.engine.screen.connection

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionTopBar() {
  TopAppBar(
    title = {
      Text(
        text = stringResource(id = R.string.connection),
        style = AppTheme.typography.headlineMedium
      )
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = AppTheme.colorScheme.primaryContainer
    )
  )
}
