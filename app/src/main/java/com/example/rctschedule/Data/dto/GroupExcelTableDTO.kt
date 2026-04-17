package com.example.rctschedule.Data.dto

import com.example.rctschedule.Data.ExcelTable
import com.example.rctschedule.Model.ScheduleMeta

data class GroupExcelTableDTO(
    val table: ExcelTable = ExcelTable(),
    val meta: ScheduleMeta = ScheduleMeta()
)