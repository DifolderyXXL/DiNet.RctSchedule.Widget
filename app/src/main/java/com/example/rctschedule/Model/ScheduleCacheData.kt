package com.example.rctschedule.Model

import com.example.rctschedule.TransformExcelTable
import java.util.Date

sealed interface ScheduleCacheData
{
    data class Ok(
        val table: ScheduleWeekData,
        val lastUpdateTime: Long) : ScheduleCacheData

    object None : ScheduleCacheData
}


data class DateRange(val from: Date, val to: Date){
    constructor() : this(Date(0), Date(0)){}
}