package com.github.whitescent.engine.utils

import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortingPreferenceModel

fun getSortedPresetList(
  list: List<PresetModel>,
  sortPreference: SortingPreferenceModel?
): List<PresetModel> {
  val preference = sortPreference ?: SortingPreferenceModel()
  return when (preference.selectedSortCategory) {
    0 -> {
      if(preference.isAscending) list.sortedBy { it.name }
      else list.sortedByDescending { it.name }
    }
    1 -> {
      if(preference.isAscending) list.sortedBy { it.gameCategory }
      else list.sortedByDescending { it.gameCategory }
    }
    2 -> {
      if(preference.isAscending) list.sortedBy { it.createdAt }
      else list.sortedByDescending { it.createdAt }
    }
    else -> list
  }
}
