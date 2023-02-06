package com.github.whitescent.engine.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class NightModeType : Parcelable {
  LIGHT, NIGHT, FOLLOW_SYSTEM
}
