package com.example.rctschedule.Di

import android.content.Context
import androidx.room.Room
import com.example.rctschedule.dao.AppDatabase
import com.example.rctschedule.dao.GroupScheduleEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideAppDatabase(@ApplicationContext app: Context) : AppDatabase
    {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java, GroupScheduleEntity.tabelName
        ).build()
    }
}