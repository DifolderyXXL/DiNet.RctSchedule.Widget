package com.example.rctschedule.Data.dto

data class ScheduleDTO(
    val schedule: GroupExcelWeeksDTO = GroupExcelWeeksDTO(),
    val updateTimeStamp: Long = 0
)