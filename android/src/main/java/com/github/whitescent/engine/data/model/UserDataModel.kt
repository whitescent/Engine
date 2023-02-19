package com.github.whitescent.engine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDataModel(
  val volumeButtonEnabled: Boolean = false,
  val hideDetails: Boolean = false,
  val buttonVibration: Boolean = false,
  val hostname: String = "",
  val selectedPreset: PresetModel? = null
) : Parcelable
