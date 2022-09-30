package com.github.nthily.engine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetsModel(
  val presetsName: String,
  val createdAt: Long
) : Parcelable
