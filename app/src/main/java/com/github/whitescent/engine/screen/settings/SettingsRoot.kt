package com.github.whitescent.engine.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoot() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
  ) {
    ListItem(
      headlineText = {
        Text(
          text = stringResource(id = R.string.settings),
          style = AppTheme.typography.headlineLarge
        )
      }
    )
    ListItem(
      headlineText = {
        Text(
          text = "行为",
          style = AppTheme.typography.labelLarge,
          color = AppTheme.colorScheme.primary
        )
      }
    )
    ListItem(
      headlineText = {
        Text(
          text = "音量键视为按钮",
          style = AppTheme.typography.titleMedium,
          modifier = Modifier.weight(1f)
        )
      },
      trailingContent = {
        Switch(
          checked = true,
          onCheckedChange = { }
        )
      }
    )
    ListItem(
      headlineText = {
        Text(
          text = "夜间模式",
          style = AppTheme.typography.titleMedium
        )
      },
      overlineText = {
      
      },
      supportingText = {
        Text(
          text = "跟随系统",
          style = AppTheme.typography.labelMedium
        )
      }
    )
  }
}
