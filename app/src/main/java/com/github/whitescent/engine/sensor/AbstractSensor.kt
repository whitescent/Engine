package com.github.whitescent.engine.sensor

abstract class AbstractSensor {

    protected var onSensorValuesChanged: ((Float) -> Unit)? = null

    abstract fun startListening()
    abstract fun stopListening()

    fun setOnSensorValuesChangedListener(listener: (Float) -> Unit) {
        onSensorValuesChanged = listener
    }

}
