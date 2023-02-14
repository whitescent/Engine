package com.github.whitescent.engine.data.repository

import com.github.whitescent.engine.data.model.PresetListModel
import com.github.whitescent.engine.data.model.PresetModel
import com.github.whitescent.engine.data.model.SortingPreferenceModel
import com.github.whitescent.engine.utils.GameCategory
import com.github.whitescent.engine.utils.getSortedPresetList
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresetsRepository @Inject constructor() {
  private val mmkv = MMKV.defaultMMKV()

  val sortingPreference = MutableStateFlow(SortingPreferenceModel())
  val presetList = MutableStateFlow<List<PresetModel>>(listOf())

  init {
    sortingPreference.value = mmkv.decodeParcelable("sorting_preference", SortingPreferenceModel::class.java)
      ?: SortingPreferenceModel()
    mmkv.decodeParcelable("preset_list", PresetListModel::class.java)?.let {
      presetList.value = getSortedPresetList(it.value, sortingPreference.value)
    }
  }

  fun isExistedInPresetList(presetName: String): Boolean {
    return presetList.value.find {
      it.name == presetName
    } != null
  }
  fun addPreset(presetName: String, gameCategory: GameCategory, currentMoment: Long) {
    presetList.value = presetList.value.toMutableList().let {
      it.add(PresetModel(presetName, gameCategory, currentMoment))
      getSortedPresetList(it, sortingPreference.value)
    }
    mmkv.encode("preset_list", PresetListModel(presetList.value))
  }
  fun updatePreset(presetListModel: PresetListModel) {
    presetList.value = getSortedPresetList(presetListModel.value, sortingPreference.value)
    mmkv.encode("preset_list", PresetListModel (presetList.value))
  }
  fun deletePreset(presetModel: PresetModel) {
    presetList.value = presetList.value.toMutableList().also {
      it.remove(presetModel)
    }
    mmkv.encode("preset_list", PresetListModel(presetList.value))
  }

  fun updateSortingPreferenceByCategory(index: Int) {
    sortingPreference.value = sortingPreference.value.copy(selectedSortCategory = index)
    mmkv.encode("sorting_preference", sortingPreference.value)
    presetList.value = getSortedPresetList(presetList.value, sortingPreference.value)
  }

  fun updateSortingPreferenceByAscending() {
    sortingPreference.value = sortingPreference.value.copy(isAscending = !sortingPreference.value.isAscending)
    mmkv.encode("sorting_preference", sortingPreference.value)
    presetList.value = getSortedPresetList(presetList.value, sortingPreference.value)
  }

}
