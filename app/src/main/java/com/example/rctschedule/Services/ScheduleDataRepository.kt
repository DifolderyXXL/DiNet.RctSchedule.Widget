package com.example.rctschedule.Services

import android.util.Log
import com.example.rctschedule.Data.TransformService
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleUpdateConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class Lce<out T> { // LCE: Loading, Content, Error
    object Loading : Lce<Nothing>()
    data class Content<T>(val data: T) : Lce<T>()
    data class Error(val throwable: Throwable) : Lce<Nothing>()

    fun contentOrNull(): T? = (this as? Content)?.data
}

@Singleton
class ScheduleDataRepository @Inject constructor(
    private val config: ScheduleUpdateConfig,
    private val scheduleFetchService: ScheduleFetchService,
    private val scheduleCacheService: ScheduleCacheService,
    private val transformService: TransformService,
    private val appSettingsRepository: ApplicationSettingsRepository
) {
    private val _scheduleState =
        MutableStateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>(Lce.Loading)

    val scheduleState : StateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>
        = _scheduleState

    public suspend fun loadSynchronously() {
        val st = System.currentTimeMillis()

        val settings = appSettingsRepository.get()

        val value = scheduleCacheService.load(settings.selectedGroup)

        if(value != null) {
            _scheduleState.value = Lce.Content(
                CacheEntry(
                    transformService
                        .Transform(value.data),
                    value.timestamp
                )
            )
        }

        Log.e("TIME", "TIME ${System.currentTimeMillis() - st}")
    }

    suspend fun requestUpdate(forceUpdate: Boolean = false) : Result<Boolean>
    {
        if(forceUpdate || shouldUpdate())
        {
            val settings = appSettingsRepository.get()

            val value = scheduleFetchService.fetchAsync(settings.selectedGroup)
            if(value.isSuccess)
            {
                val rawTable = value.getOrThrow()
                val resultValue = CacheEntry(
                    transformService.Transform(rawTable),
                    System.currentTimeMillis()
                )

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