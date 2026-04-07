package com.example.rctschedule.UseCases

import android.util.Log
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCachedScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleDataRepository
){
    suspend operator fun invoke() : ScheduleGroupWeeksData?
    {
        val currentScheduleState = scheduleRepository.cachedState.value
            ?:scheduleRepository.getCachedSchedule()

        if (currentScheduleState == null) {
            Log.e(GetCachedScheduleUseCase::class.simpleName, "Cached schedule is empty")
            return null
        }

        return currentScheduleState.data
    }
}