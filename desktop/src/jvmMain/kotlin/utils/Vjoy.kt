package utils

import com.sun.jna.Native
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference
import java.util.*

class Vjoy {

  init {
    if (vJoyEnabled) {
      println("vjoy enabled")
      println("Existing vJoy Devices: ${getNumberExistingVJD()}")
      for (i in 1..maxVjoyDevice) {
        val status = getVJDStatus(i)
        println("device status $status")
        if (status == "VJD_STAT_OWN" || status == "VJD_STAT_FREE") {
          if (_joystick.AcquireVJD(i)) {
            println("Acquired vJoy Device.")
            vJoyDeviceId = i
          } else {
            println("Failed to acquire vJoy Device.")
          }
        }
      }
    }
  }

  val vJoyEnabled get() = _joystick.vJoyEnabled()
  val vJoyVersion get() = _joystick.GetvJoyVersion()

  private fun getVJDButtonNumber(rID: Int): Int {
    return _joystick.GetVJDButtonNumber(rID)
  }

  private fun resetAll() = _joystick.ResetAll()

  private fun axisEnum(axis: String): Int {
    return when (axis.lowercase(Locale.getDefault())) {
      "x", "hid_usage_x" -> 0x30
      "y", "hid_usage_y" -> 0x31
      "z", "hid_usage_z" -> 0x32
      "rx", "hid_usage_rx" -> 0x33
      "ry", "hid_usage_ry" -> 0x34
      "rz", "hid_usage_rz" -> 0x35
      "sl0", "hid_usage_sl0" -> 0x36
      "sl1", "hid_usage_sl1" -> 0x37
      "whl", "hid_usage_whl" -> 0x38
      "pov", "hid_usage_pov" -> 0x39
      else -> 0
    }
  }

  private fun getVJDStatus(rID: Int): String {
    return when (_joystick.GetVJDStatus(rID)) {
      0 -> "VJD_STAT_OWN"
      1 -> "VJD_STAT_FREE"
      2 -> "VJD_STAT_BUSY"
      3 -> "VJD_STAT_MISS"
      4 -> "VJD_STAT_UNKN"
      else -> "VJD_STAT_ERR"
    }
  }

  fun setAxis(value: Long, axis: String): Boolean {
    return _joystick.SetAxis(value, vJoyDeviceId, axisEnum(axis))
  }

  // can be 1-128
  fun setBtn(value: Boolean, nBtn: Short): Boolean {
    return _joystick.SetBtn(value, vJoyDeviceId, nBtn)
  }

  fun getVJDAxisMax(axis: String): Long {
    val max = LongByReference()
    _joystick.GetVJDAxisMax(vJoyDeviceId, axisEnum(axis), max)
    return max.value
  }

  private fun getVJDAxisMin(rID: Int, axis: String): Long {
    val min = LongByReference()
    _joystick.GetVJDAxisMin(rID, axisEnum(axis), min)
    return min.value
  }

  private fun getVJDAxisExist(rID: Int, axis: String): Boolean {
    return _joystick.GetVJDAxisExist(rID, axisEnum(axis))
  }

  private fun getNumberExistingVJD(): Int {
    val existingDevs = IntByReference()
    return if (_joystick.GetNumberExistingVJD(existingDevs)) existingDevs.value
    else 0
  }

  companion object {
    private const val maxVjoyDevice = 16
    private val _joystick: VjoyInterface by lazy { Native.load("vJoyInterface", VjoyInterface::class.java) }
    private var vJoyDeviceId = 0
  }

}
