package com.example.rctschedule.Repositories

import com.example.rctschedule.Data.dto.ScheduleDTO
import com.example.rctschedule.Model.extensions.moveFailure
import com.example.rctschedule.Services.ScheduleCacheService
import com.example.rctschedule.Services.ScheduleFetchService
import com.example.rctschedule.Services.Time.TimeProvider
import javax.inject.Inject

class ScheduleUpdater @Inject constructor(
    private val scheduleFetchService: ScheduleFetchService,
    private val scheduleCacheService: ScheduleCacheService,
    private val timeProvider: TimeProvider
){
    suspend fun refreshFromServer(course: Int, group: Int) : Result<ScheduleDTO>{
        val result = scheduleFetchService.fetchAsync(course, group)
        if(result.isSuccess){
            val schedule = ScheduleDTO(result.getOrNull()!!, timeProvider.getCurrentDateLong())
            scheduleCacheService.save(course, group, schedule)

            return Result.success(schedule)
        }
        return result.moveFailure()
    }
}