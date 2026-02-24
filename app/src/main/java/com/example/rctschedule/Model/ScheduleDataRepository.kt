package com.example.rctschedule.Model

import android.util.Log
import androidx.compose.runtime.collectAsState
import com.example.rctschedule.Services.TransformService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class Lce<out T> { // LCE: Loading, Content, Error
    object Loading : Lce<Nothing>()
    data class Content<T>(val data: T) : Lce<T>()
    data class Error(val throwable: Throwable) : Lce<Nothing>()

    fun contentOrNull(): T? = (this as? Content)?.data
}

class ScheduleDataRepository @Inject constructor(
    private val config: ScheduleUpdateConfig,
    private val scheduleFetchService: ScheduleFetchService,
    private val scheduleCacheService: ScheduleCacheService,
    private val transformService: TransformService
) {
    private val _scheduleState =
        MutableStateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>(Lce.Loading)

    val scheduleState : StateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>
        = _scheduleState

    init {
        val value = scheduleCacheService.load()
        if(value != null) {
            _scheduleState.value = Lce.Content(
                CacheEntry(
                    transformService
                        .Transform(value.data),
                    value.timestamp
                )
            )
        }
    }

    suspend fun requestUpdate(forceUpdate: Boolean = false) : Result<Boolean>
    {
        if(forceUpdate || shouldUpdate())
        {
            val value = scheduleFetchService.fetchAsync(10-1)
            if(value.isSuccess)
            {
                val rawTable = value.getOrThrow()
                val resultValue = CacheEntry(
                    transformService.Transform(rawTable),
                    System.currentTimeMillis())

                scheduleCacheService.save(CacheEntry(rawTable, resultValue.timestamp))
                _scheduleState.value = Lce.Content(resultValue)

                return Result.success(true)
            }
            return Result.failure(value.exceptionOrNull()!!)
        }

        return Result.success(false)
    }

    fun shouldUpdate() : Boolean
    {
        when(val value = _scheduleState.value) {
            is Lce.Content -> {
                val currentTime = System.currentTimeMillis()
                return currentTime - value.data.timestamp >= config.updateTimeIntervalSeconds*1000
            }
            is Lce.Loading ->{
                return false
            }

            else -> {
                return true
            }
        }
    }
}