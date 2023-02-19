package com.github.whitescent.engine.data.model

import android.os.Parcelable
import com.github.whitescent.engine.utils.GameCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetModel(
  val name: String,
  val gameCategory: GameCategory,
  val createdAt: Long,
  val widgetList: List<WidgetModel> = listOf()
) : Parcelable
