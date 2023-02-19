package com.github.whitescent.engine.utils

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.whitescent.engine.R

enum class BottomBarItem(
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  @StringRes val label: Int
) {
  Connection(Icons.Rounded.Link, Icons.Outlined.Link, R.string.connection),
  Presets(Icons.Rounded.Folder, Icons.Outlined.Folder, R.string.presets),
  Settings(Icons.Rounded.Settings, Icons.Outlined.Settings, R.string.settings)
}
