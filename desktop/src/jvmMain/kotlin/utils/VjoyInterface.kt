package utils

import com.sun.jna.Library
import com.sun.jna.WString
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference

interface VjoyInterface : Library {

  fun vJoyEnabled(): Boolean
  fun GetvJoyVersion(): Short
  fun GetvJoyManufacturerString(): WString
  fun GetvJoyProductString(): WString
  fun GetvJoySerialNumberString(): WString
  fun DriverMatch(version1: Short?, version2: Short?): Boolean

  // What is the maximum possible number of vJoy devices
  fun GetvJoyMaxDevices(n: IntByReference): Boolean

  // What is the number of vJoy devices currently enabled
  fun GetNumberExistingVJD(n: IntByReference): Boolean

  // Get the number of buttons defined in the specified VDJ
  fun GetVJDButtonNumber(rID: Int): Int

  // Get the number of descrete-type POV hats defined in the specified VDJ
  fun GetVJDDiscPovNumber(rID: Int): Int

  // Get the number of descrete-type POV hats defined in the specified VDJ (same as the above?)
  fun GetVJDContPovNumber(rID: Int): Int

  // Test if given axis defined in the specified VDJ
  fun GetVJDAxisExist(rID: Int, axis: Int): Boolean
  fun GetVJDAxisMax(rID: Int, axis: Int, max: LongByReference): Boolean
  fun GetVJDAxisMin(rID: Int, axis: Int, min: LongByReference): Boolean

  // Get the status of the specified vJoy Device.
  fun GetVJDStatus(rID: Int): Int

  // TRUE if the specified vJoy Device exists
  fun isVJDExists(rID: Int): Boolean

  // Reurn owner's Process ID if the specified vJoy Device exists
  fun GetOwnerPid(rID: Int): Int

  // Acquire the specified vJoy Device.
  fun AcquireVJD(rID: Int): Boolean

  // Relinquish the specified vJoy Device.
  fun RelinquishVJD(rID: Int)

  // Update the position data of the specified vJoy Device.
  // Note: this function have some problem
  //fun UpdateVJD(rID: Int): Boolean

  // Write Value to a given axis defined in the specified VDJ
  fun SetAxis(value: Long, rID: Int, Axis: Int): Boolean

  // Write Value to a given button defined in the specified VDJ
  fun SetBtn(value: Boolean, rID: Int, nBtn: Short): Boolean

  // Write Value to a given descrete POV defined in the specified VDJ
  fun SetDiscPov(value: Int, rID: Int, nPov: Short): Boolean

  // Write Value to a given continuous POV defined in the specified VDJ
  fun SetContPov(value: Int, rID: Int, nPov: Short): Boolean

  // Reset all controls to predefined values in the specified VDJ
  fun ResetVJD(rID: Int): Boolean

  // Reset all controls to predefined values in all VDJ
  fun ResetAll()

  // Reset all buttons (To 0) in the specified VDJ
  fun ResetButtons(rID: Int): Boolean

  // Reset all POV Switches (To -1) in the specified VDJ
  fun ResetPovs(rID: Int): Boolean

}
