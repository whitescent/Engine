package com.github.nthily.engine.data.model

import android.os.Parcelable
import com.github.nthily.engine.screen.presets.GameItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetsModel(
  val presetsName: String,
  val gameType: GameItem,
  val createdAt: Long
) : Parcelable
