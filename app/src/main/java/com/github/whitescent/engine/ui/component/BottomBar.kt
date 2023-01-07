package com.github.whitescent.engine.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.R

@Composable
fun BottomBar(
  selectedScreen: Int,
  onClick: (targetIndex: Int) -> Unit
) {
  NavigationBar(
    contentColor = AppTheme.colorScheme.primaryContainer
  ) {
    BottomBarItem.values().forEachIndexed { index, screen ->
      NavigationBarItem(
        selected = selectedScreen == index,
        onClick = { onClick(index) },
        icon = {
          Icon(
            imageVector = if (selectedScreen == index) screen.selectedIcon else screen.unselectedIcon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
          )
        },
        label = { Text(stringResource(id = screen.label)) }
      )
    }
  }
}

enum class BottomBarItem(
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  @StringRes val label: Int
) {
  Connect(Icons.Rounded.Link, Icons.Outlined.Link, R.string.link),
  Config(Icons.Rounded.Folder, Icons.Outlined.Folder, R.string.presets),
  Settings(Icons.Rounded.Settings, Icons.Outlined.Settings, R.string.settings)
}
