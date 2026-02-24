package com.example.rctschedule.Services

import com.example.rctschedule.Model.ExcelTableMetaData
import com.example.rctschedule.Model.GroupExcelTableDTO
import com.example.rctschedule.Model.GroupExcelWeeksDTO
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.TransformWeek
import javax.inject.Inject

class TransformService @Inject constructor(private val config: TransformConfig){
    fun Transform(table: ExcelTable, group: Int) : ScheduleWeekData
    {
        return ScheduleWeekData(
            TransformWeek(table, 0),
            ExcelTableMetaData()
        )
    }

    fun Transform(table: GroupExcelWeeksDTO) : ScheduleGroupWeeksData
    {
        return ScheduleGroupWeeksData(
            table.weeks.map{ e->
                ScheduleWeekData(
                    TransformWeek(e.table, config.subjectCountingColumn),
                    e.meta)
            },
            table.group
        )
    }
}