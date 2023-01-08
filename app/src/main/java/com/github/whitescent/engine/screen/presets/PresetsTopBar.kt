package com.github.whitescent.engine.screen.presets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsTopBar() {
  TopAppBar(
    title = {
      Text(
        text = stringResource(id = R.string.presets),
        style = AppTheme.typography.headlineMedium
      )
    },
    actions = {
      IconButton(
        onClick = { /*TODO*/ }
      ) {
        Icon(Icons.Rounded.Sort, null)
      }
      IconButton(
        onClick = { /*TODO*/ }
      ) {
        Icon(Icons.Rounded.Help, null)
      }
    },
    colors = TopAppBarDefaults.smallTopAppBarColors(
      containerColor = AppTheme.colorScheme.primaryContainer
    )
  )
}
