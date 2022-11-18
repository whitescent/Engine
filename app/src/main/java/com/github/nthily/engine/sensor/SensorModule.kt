package com.github.nthily.engine.sensor

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {
    @Provides
    @Singleton
    fun provideSensor(app: Application) : AbstractSensor {
        return SensorImpl(app)
    }
}
