package com.example.rctschedule.Model

import androidx.compose.material3.DateRangePicker
import com.example.rctschedule.Services.ExcelTable
import com.example.rctschedule.TransformExcelWeek

data class ScheduleWeekData(
    val weekTable: TransformExcelWeek,
    val meta: ExcelTableMetaData
)


data class GroupExcelTableDTO(
    val table: ExcelTable,
    val meta: ExcelTableMetaData
){
    constructor() : this(ExcelTable(), ExcelTableMetaData()){}
}

data class ExcelTableMetaData(
    val group: Int,
    val dateRange: DateRange,
    val weekNumber: Int
){
    constructor() :this(0, DateRange(), 0){}
}