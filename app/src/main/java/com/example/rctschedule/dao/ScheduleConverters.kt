package com.example.rctschedule.dao

import androidx.room.TypeConverter
import com.example.rctschedule.Data.dto.GroupExcelWeeksDTO
import com.example.rctschedule.Data.dto.ScheduleDTO
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDate

class ScheduleConverters {
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    @TypeConverter
    fun fromDto(value: GroupExcelWeeksDTO): String = gson.toJson(value)

    @TypeConverter
    fun toDto(value: String): GroupExcelWeeksDTO = gson.fromJson(value, GroupExcelWeeksDTO::class.java)

    @TypeConverter
    fun fromDtoSchedule(value: ScheduleDTO): String = gson.toJson(value)

    @TypeConverter
    fun toDtoSchedule(value: String): ScheduleDTO = gson.fromJson(value, ScheduleDTO::class.java)
}

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        JsonPrimitive(src.toString())

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate =
        LocalDate.parse(json.asString)
}