package com.example.rctschedule.UseCases

import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.UseCases.Helpers.ActualSystemTime
import com.example.rctschedule.UseCases.Helpers.WidgetModeCalculator
import com.example.rctschedule.UseCases.schedule.GetScheduleUseCase
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectDisplayUseCase @Inject constructor(
    private val timeProvider: TimeProvider,
    private val displayModeRepository: WidgetDisplayModeRepository,
){
    suspend operator fun invoke(
        schedule: ScheduleGroupWeeksData,
        selectedWeekNumber: Int? = null,
        selectedDayOfWeek: DayOfWeek? = null)
    {
        val currentDate = timeProvider.getCurrentDate()

        val currentWeek = schedule.getWeekForDateSmart(currentDate)

        val currentDay = currentDate.dayOfWeek

        val newMode = WidgetModeCalculator.calculateNewMode(
            displayModeRepository.get(),
            ActualSystemTime(currentWeek?.meta?.weekNumber, currentDay),
            selectedWeekNumber,
            selectedDayOfWeek
            )

        displayModeRepository.set(newMode)
    }
}