package com.example.rctschedule.ViewModels

import com.example.rctschedule.Model.ScheduleMeta

data class WeekSelectionState(
    val availableWeeksIds: List<ScheduleMeta>,
    val selectedWeekId: Int
)