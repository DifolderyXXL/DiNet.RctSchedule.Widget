package com.example.rctschedule.Model

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ScheduleDataRepository @Inject constructor(
    private val config: ScheduleUpdateConfig,
    private val scheduleFetchService: ScheduleFetchService,
    private val scheduleCacheService: ScheduleCacheService
) {
    private val _scheduleState =
        MutableStateFlow<ScheduleCacheData>(ScheduleCacheData.None)

    val scheduleState : StateFlow<ScheduleCacheData> = _scheduleState

    init {
        _scheduleState.value = scheduleCacheService.load()
    }

    suspend fun requestUpdate(forceUpdate: Boolean = false)
    {
        if(forceUpdate || shouldUpdate())
        {
            val value = scheduleFetchService.fetchAsync() ?: return

            val resultValue = ScheduleCacheData.Ok(
                value,
                System.currentTimeMillis())

            scheduleCacheService.save(resultValue)
            _scheduleState.value = resultValue
        }
    }

    fun shouldUpdate() : Boolean
    {
        when(val value = _scheduleState.value) {
            is ScheduleCacheData.Ok -> {
                val currentTime = System.currentTimeMillis()
                return currentTime - value.lastUpdateTime >= config.updateTimeIntervalSeconds*1000
            }

            else -> {
                return true
            }
        }
    }
}