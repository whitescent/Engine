package com.github.whitescent.engine.utils

import com.github.whitescent.engine.data.model.PresetsModel
import com.github.whitescent.engine.data.model.SortPreferenceModel

fun sortPresetList(
  list: List<PresetsModel>,
  sortPreference: SortPreferenceModel?
): List<PresetsModel> {
  val preference = sortPreference ?: SortPreferenceModel()
  return when (preference.selectedSortCategory) {
    0 -> {
      if(preference.isAscending) list.sortedBy { it.presetsName }
      else list.sortedByDescending { it.presetsName }
    }
    1 -> {
      if(preference.isAscending) list.sortedBy { it.gameType }
      else list.sortedByDescending { it.gameType }
    }
    2 -> {
      if(preference.isAscending) list.sortedBy { it.createdAt }
      else list.sortedByDescending { it.createdAt }
    }
    else -> list
  }
}
