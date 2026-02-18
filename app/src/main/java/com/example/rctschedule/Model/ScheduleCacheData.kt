package com.example.rctschedule.Model

import com.example.rctschedule.Services.ExcelTable

sealed interface ScheduleCacheData
{
    data class Ok(
        val table: ExcelTable,
        val lastUpdateTime: Long) : ScheduleCacheData

    object None : ScheduleCacheData
}