package com.example.rctschedule.ViewModels

import java.time.DayOfWeek

data class DaySelectionState(
    val validDays: List<DayOfWeek>,
    val selectedDay: DayOfWeek,
    val isTodaySelected: Boolean
)