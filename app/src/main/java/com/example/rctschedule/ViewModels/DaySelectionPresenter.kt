package com.example.rctschedule.ViewModels

import com.example.rctschedule.Data.primitives.Helpers.DateRangeHelper
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.ViewModels.Targeted.DaySelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.WeekSelectionViewModel
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaySelectionPresenter @Inject constructor(
    private val timeProvider: TimeProvider,
) {
    fun present(week: WeekSelectionViewModel, day: DaySelectionViewModel): DaySelectionState {
        val validDays = DayOfWeek.entries

        //val meta = week.selected
/*
        val isCurrentWeek = DateRangeHelper.dateInRangeWithoutYear(
            timeProvider.getCurrentDate(),
            meta.dateRange
        ) || (timeProvider.getCurrentDayOfWeek() == DayOfWeek.SUNDAY
                && DateRangeHelper.dateInRangeWithoutYear(
            timeProvider.getCurrentDate().minusDays(1),
            meta.dateRange)
                )*/

        return DaySelectionState(
            validDays = validDays,
            selectedDay = day.selected,
            isTodaySelected = week.isCurrent && day.isCurrent,
            timeProvider.getCurrentDate()
        )
    }
}