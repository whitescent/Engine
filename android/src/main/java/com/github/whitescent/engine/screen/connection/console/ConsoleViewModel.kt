package com.github.whitescent.engine.screen.connection.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.data.repository.UserDataRepository
import com.github.whitescent.engine.screen.connection.port
import com.github.whitescent.engine.sensor.AbstractSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsoleViewModel @Inject constructor(
  private val sensor: AbstractSensor,
  userDataRepository: UserDataRepository
) : ViewModel() {

  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  private val connectionError = MutableStateFlow(false)

  val consoleUiState =
    combine(
      connectionError,
      userDataRepository.userData
    ) { error, userData ->
      ConsoleUiState(
        hostname = userData.hostname,
        volumeButtonEnabled = userData.volumeButtonEnabled,
        buttonVibration = userData.buttonVibration,
        error = error
      )
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = ConsoleUiState()
    )

  private val axes = MutableStateFlow(Axes())
  private val buttons = MutableStateFlow<List<Short>>(listOf())

  init {
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
    viewModelScope.launch(Dispatchers.IO) {
      val selectorManager = SelectorManager(Dispatchers.IO)
      val socket = aSocket(selectorManager).udp().connect(InetSocketAddress(consoleUiState.value.hostname, port))
      try {
        combine(
          axes,
          buttons,
          transform = ::CombinedPacket
        ).collect { combinedPacket ->
          val packet = BytePacketBuilder()
          combinedPacket.axes.let {
            packet.writeFloat(it.axisX)
            packet.writeFloat(it.axisY)
            packet.writeFloat(it.axisZ)
            packet.writeFloat(it.axisRx)
            packet.writeFloat(it.axisRy)
            packet.writeFloat(it.axisRz)
            packet.writeFloat(it.axisSl0)
            packet.writeFloat(it.axisSl1)
          }
          val buttonsSize = buttons.value.size.toShort()
          packet.writeShort(buttonsSize)
          for (index in 0 until buttonsSize) {
            packet.writeShort(buttons.value[index])
          }
          socket.outgoing.send(Datagram(packet.build(), InetSocketAddress(consoleUiState.value.hostname, port)))
        }
      } catch (e: Exception) {
        connectionError.emit(true)
        socket.close()
        selectorManager.close()
        e.printStackTrace()
      }
    }
  }

  fun startListeningSensor() = sensor.startListening()
  fun stopListeningSensor() = sensor.stopListening()

  fun updateAxisX(value: Float) { axes.value = axes.value.copy(axisX = value) }
  fun updateAxisY(value: Float) { axes.value = axes.value.copy(axisY = value) }
  fun updateAxisZ(value: Float) { axes.value = axes.value.copy(axisZ = value) }
  fun updateAxisRx(value: Float) { axes.value = axes.value.copy(axisRx = value) }
  fun updateAxisRy(value: Float) { axes.value = axes.value.copy(axisRy = value) }
  fun updateAxisRz(value: Float) { axes.value = axes.value.copy(axisRz = value) }
  fun updateAxisSl0(value: Float) { axes.value = axes.value.copy(axisSl0 = value) }
  fun updateAxisSl1(value: Float) { axes.value = axes.value.copy(axisSl1 = value) }

  fun initButtons(value: Int) {
    val count = if (consoleUiState.value.volumeButtonEnabled) value + 2 else value
    buttons.value = buttons.value.toMutableList().also {
      for(index in 0 until count) {
        it.add(0)
      }
    }
  }
  fun resetButtons() {
    buttons.value = listOf()
  }

  fun updateButton(index: Short, pressed: Short) {
    buttons.value = buttons.value.toMutableList().also {
      it[index.toInt()] = pressed
    }
  }
}

data class ConsoleUiState(
  val volumeButtonEnabled: Boolean = false,
  val buttonVibration: Boolean = false,
  val error: Boolean = false,
  val hostname: String = ""
)

data class CombinedPacket(
  val axes: Axes,
  val buttons: List<Short>
)

data class Axes(
  val axisX: Float = 0f,
  val axisY: Float = 0f,
  val axisZ: Float = 0f,
  val axisRx: Float = 0f,
  val axisRy: Float = 0f,
  val axisRz: Float = 0f,
  val axisSl0: Float = 0f,
  val axisSl1: Float = 0f
)
