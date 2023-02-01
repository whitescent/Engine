package com.github.whitescent.engine.screen.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.whitescent.engine.sensor.AbstractSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Socket
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
  private val sensor: AbstractSensor
) : ViewModel() {

  private val _sensorFlow = MutableStateFlow(0f)
  val sensorFlow = _sensorFlow.asStateFlow()

  init {
    sensor.startListening()
    sensor.setOnSensorValuesChangedListener {
      _sensorFlow.value = it
    }
  }

  fun connect() {
    viewModelScope.launch(Dispatchers.IO) {
      val selectorManager = SelectorManager(Dispatchers.IO)
      val socket = aSocket(selectorManager).tcp().connect("192.168.2.4", 9002)
      try {
        withContext(Dispatchers.IO) {
          val receiveChannel = socket.openReadChannel()
          val sendChannel = socket.openWriteChannel(autoFlush = true)
          while (true) {
            sendChannel.writeFloat(_sensorFlow.value)
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
        socket.close()
        selectorManager.close()
      }
    }
  }
}
