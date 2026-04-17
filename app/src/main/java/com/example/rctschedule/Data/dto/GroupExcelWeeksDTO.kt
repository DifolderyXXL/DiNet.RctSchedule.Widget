package com.example.rctschedule.Data.dto

data class GroupExcelWeeksDTO(
    val weeks: List<GroupExcelTableDTO> = emptyList(),
    val group: Int = 0
)