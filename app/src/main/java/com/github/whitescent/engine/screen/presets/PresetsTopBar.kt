package com.github.whitescent.engine.screen.presets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.github.whitescent.engine.AppTheme
import com.github.whitescent.engine.R
import com.github.whitescent.engine.data.model.SortPreferenceModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsTopBar(
  preference: SortPreferenceModel,
  onClickSortCategory: (Int) -> Unit,
  onSortingChanged: () -> Unit
) {
  var isOpen by remember { mutableStateOf(false) }
  val radioGroup = listOf("名称", "游戏类别", "创建时间")
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
          radioGroup.forEachIndexed { index, name ->
            DropdownMenuItem(
              text = { Text(name) },
              onClick = {
                onClickSortCategory(index)
                isOpen = false
              },
              trailingIcon = {
                Icon(
                  imageVector =
                    if (preference.selectedSortCategory == index) Icons.Rounded.RadioButtonChecked
                    else Icons.Rounded.RadioButtonUnchecked,
                  contentDescription = null
                )
              },
            )
          }
          DropdownMenuItem(
            text = { Text(stringResource(R.string.ascending)) },
            onClick = onSortingChanged,
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
