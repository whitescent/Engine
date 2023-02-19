package com.github.whitescent.engine.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun Context.vibrate(time: Long) {
  val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    vibratorManager.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    getSystemService(VIBRATOR_SERVICE) as Vibrator
  }
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
  } else {
    @Suppress("DEPRECATION")
    vibrator.vibrate(time)
  }
}
