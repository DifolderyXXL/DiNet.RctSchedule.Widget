package com.example.rctschedule.UseCases

import android.util.Log
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.UseCases.Helpers.ActualSystemTime
import com.example.rctschedule.UseCases.Helpers.WidgetModeCalculator
import com.example.rctschedule.Views.Callbacks.WeekSelectActionCallback
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectDisplayUseCase @Inject constructor(
    private val timeProvider: TimeProvider,
    private val displayModeRepository: WidgetDisplayModeRepository,
    private val scheduleRepository: ScheduleDataRepository,
){
    suspend operator fun invoke(
        selectedWeekNumber: Int? = null,
        selectedDayOfWeek: DayOfWeek? = null)
    {
        val currentDate = timeProvider.getCurrentDate()

        val schedule = scheduleRepository.getCachedSchedule()
            ?: return

        val currentWeek = schedule.data.getWeekForDateSmart(currentDate)

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