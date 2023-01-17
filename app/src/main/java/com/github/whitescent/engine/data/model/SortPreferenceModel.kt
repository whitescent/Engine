package com.github.whitescent.engine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortPreferenceModel(
  val selectedSortCategory: Int = 0,
  val isAscending: Boolean = false
) : Parcelable
