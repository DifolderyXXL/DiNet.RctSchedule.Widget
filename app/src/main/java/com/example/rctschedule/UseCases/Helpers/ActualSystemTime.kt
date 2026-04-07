package com.example.rctschedule.UseCases.Helpers

import java.time.DayOfWeek

data class ActualSystemTime(
    val currentWeekNumber: Int?,
    val currentDayOfWeek: DayOfWeek
)