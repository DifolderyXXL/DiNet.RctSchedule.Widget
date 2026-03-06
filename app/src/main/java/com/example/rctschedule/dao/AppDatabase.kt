package com.example.rctschedule.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GroupScheduleEntity::class], version = 1)
@TypeConverters(ScheduleConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}

