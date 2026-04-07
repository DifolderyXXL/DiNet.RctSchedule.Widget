package com.example.rctschedule.Model

import com.example.rctschedule.Data.ExcelTable
import com.example.rctschedule.Helpers.DateRangeHelper
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.TransformExcelWeek
import java.time.DayOfWeek
import java.time.LocalDate

data class ScheduleDayData(
    val weekTable: TransformExcelDayTable,
    val day: DayOfWeek
)

data class ScheduleWeekData(
    val weekTable: TransformExcelWeek,
    val meta: ScheduleMeta
)

data class ScheduleGroupWeeksData(
    val weeks: List<ScheduleWeekData>,
    val group: Int
){
    public fun getWeek(weekNumber: Int) : ScheduleWeekData?
    {
        return weeks.firstOrNull{it.meta.weekNumber == weekNumber}
    }

    public fun getWeekForDay(date: LocalDate) : ScheduleWeekData?
    {
        return weeks.firstOrNull{ DateRangeHelper.Companion.dateInRangeWithoutYear(date, it.meta.dateRange)}
    }

    fun getWeekForDateSmart(targetDate: LocalDate): ScheduleWeekData? {

        val directMatch = getWeekForDay(targetDate)

        if (directMatch != null) return directMatch

        if (targetDate.dayOfWeek == DayOfWeek.SUNDAY) {
            val saturdayDate = targetDate.minusDays(1)

            return getWeekForDay(saturdayDate)
        }
        return null
    }
}

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