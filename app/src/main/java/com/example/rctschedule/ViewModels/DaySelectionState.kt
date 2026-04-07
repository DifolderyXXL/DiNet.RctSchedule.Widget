package com.example.rctschedule.ViewModels

import java.time.DayOfWeek
import java.time.LocalDate

data class DaySelectionState(
    val validDays: List<DayOfWeek>,
    val selectedDay: DayOfWeek,
    val isTodaySelected: Boolean,
    val currentDate: LocalDate
)