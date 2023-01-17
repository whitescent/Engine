package com.github.whitescent.engine.data.model

import android.os.Parcelable
import com.github.whitescent.engine.screen.presets.GameItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetsModel(
  val presetsName: String,
  val gameType: GameItem,
  val createdAt: Long,
  val widgetList: List<WidgetModel> = listOf()
) : Parcelable

@Parcelize
data class PresetsListModel(
  val value: List<PresetsModel> = listOf()
) : Parcelable