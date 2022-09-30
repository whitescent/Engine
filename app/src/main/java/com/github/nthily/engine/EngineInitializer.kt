package com.github.nthily.engine

import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV

class EngineInitializer: Initializer<Unit> {
  override fun create(context: Context) {
    MMKV.initialize(context)
  }

  override fun dependencies(): List<Class<out Initializer<*>>> {
    return emptyList()
  }
}
