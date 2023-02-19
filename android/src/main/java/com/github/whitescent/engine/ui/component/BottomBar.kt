package com.github.whitescent.engine.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.whitescent.engine.utils.BottomBarItem

@Composable
fun BottomBar(
  selectedScreen: Int,
  onClick: (targetIndex: Int) -> Unit
) {
  NavigationBar {
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
