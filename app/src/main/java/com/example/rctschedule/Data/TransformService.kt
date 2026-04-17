package com.example.rctschedule.Data

import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Data.dto.GroupExcelWeeksDTO
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.TransformWeek
import javax.inject.Inject

class TransformService @Inject constructor(private val config: TransformConfig){
    fun Transform(table: ExcelTable, group: Int) : ScheduleWeekData
    {
        return ScheduleWeekData(
            TransformWeek(table, 0),
            ScheduleMeta()
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