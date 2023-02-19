package com.github.whitescent.engine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetListModel(
  val value: List<PresetModel> = listOf()
) : Parcelable
