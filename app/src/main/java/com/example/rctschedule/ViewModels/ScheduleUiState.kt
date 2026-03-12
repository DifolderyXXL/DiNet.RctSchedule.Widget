package com.example.rctschedule.ViewModels

import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.TransformExcelDayTable
import java.time.DayOfWeek

data class ScheduleUiState(
    val anyContent: Boolean = false,
    val isLoading: Boolean = false,

    val currentDayName: DayOfWeek = DayOfWeek.MONDAY,
    val tableMeta: ScheduleMeta = ScheduleMeta(),
    val isDayOff: Boolean = false,
    val group: Int = -1,
    val day: TransformExcelDayTable = TransformExcelDayTable(emptyList()),

    val cacheInstance: CacheEntry<ScheduleGroupWeeksData>? = null
)