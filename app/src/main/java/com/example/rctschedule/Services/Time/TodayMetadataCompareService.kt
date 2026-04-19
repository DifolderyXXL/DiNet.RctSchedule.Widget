package com.example.rctschedule.Services.Time

import com.example.rctschedule.Data.primitives.Helpers.DateRangeHelper
import com.example.rctschedule.Model.ScheduleMeta
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodayMetadataCompareService @Inject constructor(
    val timeProvider: TimeProvider){
    fun isCurrentWeek(meta: ScheduleMeta) : Boolean{
        val isCurrentWeek = DateRangeHelper.Companion.dateInRangeWithoutYear(
            timeProvider.getCurrentDate(),
            meta.dateRange
        ) || (timeProvider.getCurrentDayOfWeek() == DayOfWeek.SUNDAY
                && DateRangeHelper.Companion.dateInRangeWithoutYear(
            timeProvider.getCurrentDate().minusDays(1),
            meta.dateRange)
                )

        return isCurrentWeek
    }

    fun isToday(day: DayOfWeek) : Boolean
        = day == timeProvider.getCurrentDayOfWeek()
}