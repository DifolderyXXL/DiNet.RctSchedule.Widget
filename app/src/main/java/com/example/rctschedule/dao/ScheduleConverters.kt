package com.example.rctschedule.dao

import androidx.room.TypeConverter
import com.example.rctschedule.Model.GroupExcelWeeksDTO
import com.google.gson.Gson

class ScheduleConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromDto(value: GroupExcelWeeksDTO): String = gson.toJson(value)

    @TypeConverter
    fun toDto(value: String): GroupExcelWeeksDTO = gson.fromJson(value, GroupExcelWeeksDTO::class.java)
}