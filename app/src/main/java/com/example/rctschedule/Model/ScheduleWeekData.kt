package com.example.rctschedule.Model

import com.example.rctschedule.Data.ExcelTable
import com.example.rctschedule.TransformExcelWeek

data class ScheduleWeekData(
    val weekTable: TransformExcelWeek,
    val meta: ScheduleMeta
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
    val meta: ScheduleMeta
){
    constructor() : this(ExcelTable(), ScheduleMeta()){}
}

data class ScheduleMeta(
    val dateRange: DateRange,
    val weekNumber: Int
){
    constructor() :this(DateRange(), 0){}
}