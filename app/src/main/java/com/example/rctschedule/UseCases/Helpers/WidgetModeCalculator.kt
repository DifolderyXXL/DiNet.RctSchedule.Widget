package com.example.rctschedule.UseCases.Helpers

import android.util.Log
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.UseCases.SelectDisplayUseCase
import java.time.DayOfWeek

object WidgetModeCalculator {
    fun calculateNewMode(
        currentMode: WidgetDisplayMode,
        actualTime: ActualSystemTime,
        newWeekNumber: Int? = null,
        newDayOfWeek: DayOfWeek? = null
    ) : WidgetDisplayMode {

        val (wasWeekId, wasDay) = when(currentMode){
            is WidgetDisplayMode.Fixed ->
                currentMode.weekId to currentMode.dayOfWeek
            is WidgetDisplayMode.FollowCurrent ->
                actualTime.currentWeekNumber to actualTime.currentDayOfWeek
        }

        val targetWeek = newWeekNumber ?: wasWeekId
        val targetDay = newDayOfWeek ?: wasDay

        if(targetWeek == null)
        {
            return currentMode
        }

        return if(targetDay == actualTime.currentDayOfWeek && targetWeek == actualTime.currentWeekNumber)
            WidgetDisplayMode.FollowCurrent
        else
            WidgetDisplayMode.Fixed(targetWeek, targetDay)
    }
}