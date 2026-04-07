package com.example.rctschedule.Services.Time

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

interface TimeProvider{
    fun getCurrentDate() : LocalDate
    public fun getCurrentDayOfWeek() : DayOfWeek
}

class NowTimeProvider @Inject constructor() : TimeProvider{
    override fun getCurrentDate() : LocalDate {
        return LocalDate.now()
    }

    override fun getCurrentDayOfWeek() : DayOfWeek {
        return getCurrentDate().dayOfWeek
    }
}

class MockTimeProvider(private var fixedDate: LocalDate) : TimeProvider {

    override fun getCurrentDate(): LocalDate = fixedDate
    override fun getCurrentDayOfWeek(): DayOfWeek = fixedDate.dayOfWeek
}