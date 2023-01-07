package com.github.whitescent.engine.sensor

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME

class SensorImpl(
    context: Context
) : AbstractSensor(), SensorEventListener {

    private val _sensorManager = context.applicationContext
        .getSystemService(SENSOR_SERVICE) as SensorManager

    private var _accelerometer: Sensor? = null
    private var _magnetometer: Sensor? = null
    private var _gamometer: Sensor? = null
    private var _supportedSensorLevel = SupportedSensorLevel.NONE

    private var _gravity = FloatArray(3)
    private var _geomagnetic = FloatArray(3)
    private var _rotation = FloatArray(3)
    private val _matrix = FloatArray(9)
    private val _orientation = FloatArray(3)

    init {
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        _magnetometer = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        _gamometer = _sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        when {
            _gamometer != null -> _supportedSensorLevel = SupportedSensorLevel.FULL
            _accelerometer != null && _magnetometer != null -> _supportedSensorLevel = SupportedSensorLevel.MEDIUM
            _magnetometer == null -> _supportedSensorLevel = SupportedSensorLevel.BASIC
        }
    }

    override fun startListening() {
        _accelerometer?.let {
            _sensorManager.registerListener(this, it, SENSOR_DELAY_GAME)
        }
        _magnetometer?.let {
            _sensorManager.registerListener(this, it, SENSOR_DELAY_GAME)
        }
        _gamometer?.let {
            _sensorManager.registerListener(this, it, SENSOR_DELAY_GAME)
        }
    }

    override fun stopListening() {
        _sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (_supportedSensorLevel == SupportedSensorLevel.NONE) return
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> _gravity = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> _geomagnetic = event.values
            Sensor.TYPE_GAME_ROTATION_VECTOR -> _rotation = event.values
        }
        val steering = when (_supportedSensorLevel) {
            SupportedSensorLevel.BASIC -> {
                ((1 + (_gravity[1] / SensorManager.STANDARD_GRAVITY)) / 2)
            }
            SupportedSensorLevel.MEDIUM -> {
                SensorManager.getRotationMatrix(
                    _matrix, null, _gravity, _geomagnetic
                )
                SensorManager.getOrientation(_matrix, _orientation)
                (((Math.PI / 2) - _orientation[1]) / Math.PI).toFloat()
            }
            SupportedSensorLevel.FULL -> {
                SensorManager.getRotationMatrixFromVector(_matrix, _rotation)
                SensorManager.getOrientation(_matrix, _orientation)
                (((Math.PI / 2) - _orientation[1]) / Math.PI).toFloat()
            }
            else -> 0.5F
        }.coerceIn(0.0F, 1.0F)
        onSensorValuesChanged?.invoke(steering)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    enum class SupportedSensorLevel {
        NONE, BASIC, MEDIUM, FULL
    }
}
