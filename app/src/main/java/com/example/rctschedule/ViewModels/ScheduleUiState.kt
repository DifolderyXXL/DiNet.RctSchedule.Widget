package com.example.rctschedule.ViewModels

import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.UseCases.WidgetDisplayData
import java.time.DayOfWeek

data class ScheduleUiState(
    val selectWeek: WeekSelectState = WeekSelectState(),
    val selectDay: DaySelectState = DaySelectState(),
    val content: ContentState? = null
)

data class ContentState(
    val group: Int,
    val displayData: WidgetDisplayData?,
    val timestamp: Long = 0,
)

data class WeekSelectState(
    val weeksMetas: List<ScheduleMeta> = emptyList(),
    val selectedWeek: ScheduleMeta = ScheduleMeta(),
)

data class DaySelectState(
    val days: List<DayOfWeek> = emptyList(),
    val selectedDay: DayOfWeek = DayOfWeek.MONDAY,
)