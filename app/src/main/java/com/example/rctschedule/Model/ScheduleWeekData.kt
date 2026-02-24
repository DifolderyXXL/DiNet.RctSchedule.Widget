package com.example.rctschedule.Model

import com.example.rctschedule.Services.ExcelTable
import com.example.rctschedule.TransformExcelWeek

data class ScheduleWeekData(
    val weekTable: TransformExcelWeek,
    val meta: ExcelTableMetaData
)

data class ScheduleGroupWeeksData(
    val weeks: List<ScheduleWeekData>,
    val group: Int
)

data class GroupExcelWeeksDTO(
    val weeks: List<GroupExcelTableDTO>,
    val group: Int
){
    constructor() :this(emptyList<GroupExcelTableDTO>(), 0){}
}

data class GroupExcelTableDTO(
    val table: ExcelTable,
    val meta: ExcelTableMetaData
){
    constructor() : this(ExcelTable(), ExcelTableMetaData()){}
}

data class ExcelTableMetaData(
    val dateRange: DateRange,
    val weekNumber: Int
){
    constructor() :this(DateRange(), 0){}
}