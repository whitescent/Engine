package com.github.whitescent.engine.screen.presets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.R
import com.github.whitescent.engine.data.model.SortingPreferenceModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsTopBar(
  preference: SortingPreferenceModel,
  onClickSortCategory: (Int) -> Unit,
  onSortingChanged: () -> Unit,
  openHelpDialog: () -> Unit
) {
  var isOpen by remember { mutableStateOf(false) }
  val sortingGroup = listOf(
    stringResource(id = R.string.preset_name),
    stringResource(id = R.string.game_category),
    stringResource(id = R.string.date),
  )
  TopAppBar(
    title = {
      Text(
        text = stringResource(id = R.string.presets),
        style = AppTheme.typography.headlineMedium
      )
    },
    actions = {
      Column {
        IconButton(
          onClick = { isOpen = true }
        ) {
          Icon(Icons.Rounded.Sort, null)
        }
        DropdownMenu(
          expanded = isOpen,
          onDismissRequest = { isOpen = false },
        ) {
          sortingGroup.forEachIndexed { index, name ->
            DropdownMenuItem(
              text = { Text(name) },
              onClick = {
                onClickSortCategory(index)
                isOpen = false
              },
              trailingIcon = {
                Icon(
                  imageVector = if (preference.selectedSortCategory == index) Icons.Rounded.RadioButtonChecked
                  else Icons.Rounded.RadioButtonUnchecked,
                  contentDescription = null
                )
              },
            )
          }
          DropdownMenuItem(
            text = { Text(stringResource(R.string.ascending)) },
            onClick = {
              onSortingChanged()
              isOpen = false
            },
            trailingIcon = {
              Icon(
                imageVector =
                if (preference.isAscending) Icons.Rounded.CheckBox
                else Icons.Rounded.CheckBoxOutlineBlank,
                contentDescription = null
              )
            },
          )
        }
      }
      IconButton(
        onClick = openHelpDialog
      ) {
        Icon(Icons.Rounded.Help, null)
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = AppTheme.colorScheme.primaryContainer
    )
  )
}
