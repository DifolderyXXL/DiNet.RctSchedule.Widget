package com.example.rctschedule.Repositories

import androidx.compose.ui.res.colorResource
import com.example.rctschedule.Data.dto.ScheduleDTO
import com.example.rctschedule.Model.extensions.moveFailure
import com.example.rctschedule.Model.extensions.nextOnFailure
import com.example.rctschedule.Model.extensions.nextOnFailureAsync
import com.example.rctschedule.Model.extensions.nextOnSuccess
import com.example.rctschedule.Services.ScheduleCacheService
import com.example.rctschedule.Services.ScheduleFetchService
import com.example.rctschedule.Services.Time.TimeProvider
import javax.inject.Inject

sealed class FetchPolicy{
    object CacheOnly : FetchPolicy()
    object NetworkOnly : FetchPolicy()
    object CacheFirst : FetchPolicy()
}

class ScheduleRepository @Inject constructor(
    private val scheduleCacheService: ScheduleCacheService,
    private val scheduleUpdater: ScheduleUpdater
) {
    suspend fun getAsync(course: Int, group: Int, policy: FetchPolicy) : Result<ScheduleDTO>{
        return when(policy){
            FetchPolicy.NetworkOnly -> fetchAndCacheAsync(course, group)
            FetchPolicy.CacheOnly -> getCacheAsync(course, group)
            FetchPolicy.CacheFirst -> {
                getCacheAsync(course, group).nextOnFailureAsync {
                    fetchAndCacheAsync(course, group)
                }
            }
        }
    }

    private suspend fun getCacheAsync(course: Int,
                                      group: Int
    ): Result<ScheduleDTO>{
        return scheduleCacheService.load(course, group)
    }

    private suspend fun fetchAndCacheAsync(
        course: Int,
        group: Int
    ): Result<ScheduleDTO> {
        return scheduleUpdater.refreshFromServer(course, group)
    }

}

