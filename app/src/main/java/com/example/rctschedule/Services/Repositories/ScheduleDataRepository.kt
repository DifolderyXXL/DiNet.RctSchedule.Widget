package com.example.rctschedule.Services.Repositories

import android.util.Log
import com.example.rctschedule.Data.TransformService
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Services.ScheduleCacheService
import com.example.rctschedule.Services.ScheduleFetchService
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

class ValueIsEmptyException : Throwable()
class CannotFetchScheduleException : Throwable()

@Singleton
class ScheduleDataRepository @Inject constructor(
    private val scheduleFetchService: ScheduleFetchService,
    private val scheduleCacheService: ScheduleCacheService,
    private val transformService: TransformService,
    private val appSettingsRepository: ApplicationSettingsRepository
) {
    private val _scheduleState =
        MutableStateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>(Lce.Loading)

    private val _cachedState =
        MutableStateFlow<CacheEntry<ScheduleGroupWeeksData>?>(null)

    val cachedState : StateFlow<CacheEntry<ScheduleGroupWeeksData>?>
        =_cachedState
    val scheduleState : StateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>
        = _scheduleState

    suspend fun fetchScheduleDirectly(forceUpdate: Boolean): Result<CacheEntry<ScheduleGroupWeeksData>> {
        return withContext(Dispatchers.IO) {
            val result = if (!forceUpdate) {
                loadFromCache().recoverCatching { tryLoadFromNetworkAndSave().getOrThrow() }
            } else {
                tryLoadFromNetworkAndSave()
            }

            result.getOrNull()?.let {
                _cachedState.value = it
                _scheduleState.value = Lce.Content(it)
            }
            result
        }
    }

    suspend fun getCachedSchedule(): CacheEntry<ScheduleGroupWeeksData>? {
        _cachedState.value?.let { return it }

        return try {
            loadFromCache().getOrNull()
        } catch (e: Exception) {
            Log.e(ScheduleDataRepository::class.simpleName, "Cache load failed", e)
            null
        }
    }

    public suspend fun loadSchedule(forceUpdate: Boolean){

        withContext(Dispatchers.IO) {
            _scheduleState.value = Lce.Loading

            var cacheState: Result<CacheEntry<ScheduleGroupWeeksData>>
            if (!forceUpdate) {
                cacheState = loadFromCache().recoverCatching { tryLoadFromNetworkAndSave().getOrThrow() }

                /*if (cacheState.isFailure) {
                    cacheState = tryLoadFromNetworkAndSave()
                }*/
            } else {
                cacheState = tryLoadFromNetworkAndSave()
            }

            if (cacheState.isFailure) {
                _scheduleState.value = Lce.Error(cacheState.exceptionOrNull()!!)
                return@withContext
            }

            _cachedState.value = cacheState.getOrNull()
            _scheduleState.value = Lce.Content(cacheState.getOrThrow())
        }
    }

    private suspend fun loadFromCache() : Result<CacheEntry<ScheduleGroupWeeksData>> {
        val settings = appSettingsRepository.get()

        val value = scheduleCacheService.load(settings.selectedGroup)

        if(value != null) {
            val data = CacheEntry(
                transformService
                    .Transform(value.data),
                value.timestamp
            )
            return Result.success(data)
        }

        return Result.failure(ValueIsEmptyException())
    }

    private suspend fun tryLoadFromNetworkAndSave() : Result<CacheEntry<ScheduleGroupWeeksData>>
    {
        val settings = appSettingsRepository.get()

        val value = scheduleFetchService.fetchAsync(settings.selectedGroup)
        if(value.isSuccess)
        {
            val rawTable = value.getOrNull()
                ?: return Result.failure(ValueIsEmptyException())


            val resultValue = CacheEntry(
                transformService.Transform(rawTable),
                System.currentTimeMillis()
            )

            val state = CacheEntry(rawTable, resultValue.timestamp)
            scheduleCacheService.save(state)


            return Result.success(resultValue)
        }
        return Result.failure(value.exceptionOrNull() ?: CannotFetchScheduleException())
    }
}