package com.github.whitescent.engine.utils

import androidx.annotation.StringRes
import com.github.whitescent.engine.R

enum class GameCategory(
  val painter: Int,
  @StringRes val gameName: Int
) {
  Undefined(R.drawable.other_preset, R.string.uncategorized),
  AssettoCorsa(R.drawable.assetto_corsa, R.string.assetto_corsa),
  Forza(R.drawable.forza, R.string.forza_series),
  F1(R.drawable.f1, R.string.f1_series),
  Dirt(R.drawable.dirt, R.string.dirt_series),
  BeamNGDrive(R.drawable.beamng, R.string.beamng_drive)
}
