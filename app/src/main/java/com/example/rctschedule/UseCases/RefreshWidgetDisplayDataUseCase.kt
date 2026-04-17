package com.example.rctschedule.UseCases

import com.example.rctschedule.Model.ScheduleDayData
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.Services.Time.TimeProvider
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshWidgetDisplayDataUseCase @Inject constructor(
    private val timeProvider: TimeProvider,
    private val displayModeRepository: WidgetDisplayModeRepository,
){
    suspend operator fun invoke(schedule: ScheduleGroupWeeksData) : Result<WidgetDisplayData> {
        val date = getCurrentDay(schedule, timeProvider)

        if(date != null)
        {
            val inst = WidgetDisplayMode.FollowCurrent
            val current = getCurrentDay(schedule, timeProvider)
                ?: return Result.failure(InvalidScheduleException())

            displayModeRepository.set(inst)

            return Result.success(current)
        }
        else{
            val week = schedule.weeks.firstOrNull()
            val day = week?.weekTable?.days?.firstOrNull()
                ?: return Result.failure(InvalidScheduleException())

            val inst = WidgetDisplayMode.Fixed(
                week.meta.weekNumber,
                DayOfWeek.MONDAY)
            displayModeRepository.set(inst)

            return Result.success(
                WidgetDisplayData(
                    week,
                    ScheduleDayData(day, DayOfWeek.MONDAY)))
        }

    }
}