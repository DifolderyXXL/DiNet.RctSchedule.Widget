package com.example.rctschedule.ViewModels

import com.example.rctschedule.Helpers.DateRangeHelper
import com.example.rctschedule.Services.Time.TimeProvider
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaySelectionPresenter @Inject constructor(
    private val timeProvider: TimeProvider,
) {
    fun present(week: WeekSelectState, day: DaySelectState): DaySelectionState {
        val validDays = DayOfWeek.entries

        val meta = week.selectedWeek

        val isCurrentWeek = DateRangeHelper.dateInRangeWithoutYear(
            timeProvider.getCurrentDate(),
            meta.dateRange
        ) || (timeProvider.getCurrentDayOfWeek() == DayOfWeek.SUNDAY
                && DateRangeHelper.dateInRangeWithoutYear(
            timeProvider.getCurrentDate().minusDays(1),
            meta.dateRange)
                )

        return DaySelectionState(
            validDays = validDays,
            selectedDay = day.selectedDay,
            isTodaySelected = isCurrentWeek && day.selectedDay == timeProvider.getCurrentDate().dayOfWeek,
            timeProvider.getCurrentDate()
        )
    }
}