package network

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import utils.Vjoy

class MySocket {

  private val _socketUiState = MutableStateFlow(SocketUiState())
  val socketUiState = _socketUiState.asStateFlow()

  private var timer: Flow<Int> = (0..Int.MAX_VALUE).asSequence().asFlow()
  private var latestReceivedDataTime = 0

  suspend fun init(vjoy: Vjoy) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
      timer
        .onEach { delay(1_000) }
        .collect {
          if (it % 2 == 0) {
            if (latestReceivedDataTime == _socketUiState.value.time) {
              _socketUiState.value = SocketUiState()
            }
          }
        }
    }
    scope.launch {
      receiveData(vjoy)
    }
  }

  private suspend fun receiveData(vjoy: Vjoy) {
    while (true) {
      withContext(Dispatchers.IO) {
        try {
          val input = serverSocket.receive()
          val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).nanosecond
          _socketUiState.value = _socketUiState.value.copy(receivedData = true, time = time)
          latestReceivedDataTime = time

          val axisX = input.packet.readFloat()
          val axisY = input.packet.readFloat()
          val axisZ = input.packet.readFloat()
          val axisRx = input.packet.readFloat()
          val axisRy = input.packet.readFloat()
          val axisRz = input.packet.readFloat()
          val axisSl0 = input.packet.readFloat()
          val axisSl1 = input.packet.readFloat()

          vjoy.setAxis((vjoy.getVJDAxisMax("x") * axisX).toLong(), "x")
          vjoy.setAxis((vjoy.getVJDAxisMax("Y") * axisY).toLong(), "Y")
          vjoy.setAxis((vjoy.getVJDAxisMax("Z") * axisZ).toLong(), "Z")
          vjoy.setAxis((vjoy.getVJDAxisMax("rx") * axisRx).toLong(), "rx")
          vjoy.setAxis((vjoy.getVJDAxisMax("ry") * axisRy).toLong(), "ry")
          vjoy.setAxis((vjoy.getVJDAxisMax("rz") * axisRz).toLong(), "rz")
          vjoy.setAxis((vjoy.getVJDAxisMax("sl0") * axisSl0).toLong(), "sl0")
          vjoy.setAxis((vjoy.getVJDAxisMax("sl1") * axisSl1).toLong(), "sl1")

          val buttonCount = input.packet.readShort()
          if (buttonCount.toInt() != 0) {
            for (index in 0 until buttonCount) {
              val buttonValue = input.packet.readShort()
              vjoy.setBtn(buttonValue.toInt() != 0, (index + 1).toShort())
            }
          }
        } catch (e: Exception) {
          serverSocket.close()
          e.printStackTrace()
        }
      }
    }
  }

  companion object {
    private const val PORT = 12345
    private val selectorManager = SelectorManager(Dispatchers.IO)
    private val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", PORT))
  }

}

data class SocketUiState(
  val receivedData: Boolean = false,
  val time: Int = 0
)
