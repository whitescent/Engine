package com.github.whitescent.engine.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tencent.mmkv.MMKV

class ThemeManager {

  private val mmkv = MMKV.defaultMMKV()
  var nightMode by mutableStateOf(NightModeType.FOLLOW_SYSTEM)
    private set

  init {
    nightMode = mmkv.decodeParcelable("night_mode", NightModeType::class.java)
      ?: NightModeType.FOLLOW_SYSTEM
  }

  fun updateNightMode(type: NightModeType) {
    nightMode = type
    mmkv.encode("night_mode", type)
  }
}
