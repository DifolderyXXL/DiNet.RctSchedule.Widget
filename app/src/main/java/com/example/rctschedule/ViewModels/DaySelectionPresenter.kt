package com.example.rctschedule.ViewModels

import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.UseCases.ScheduleNavigationUseCase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaySelectionPresenter @Inject constructor(
    private val navigationUseCase: ScheduleNavigationUseCase
) {
    fun present(schedule: ScheduleGroupWeeksData?, currentDay: DayOfWeek): DaySelectionState {
        val currentWeekId = navigationUseCase.provideCurrentWeekOrAny()?.weekNumber

        val validDays = schedule?.weeks
            ?.find { it.meta.weekNumber == currentWeekId }
            ?.weekTable?.days?.mapIndexed{ i, it -> DayOfWeek.of(i+1) }
            ?: DayOfWeek.entries

        val meta = schedule?.weeks?.firstOrNull{
            it.meta.weekNumber == currentWeekId
        }?.meta

        var isCurrentWeek = false
        if(meta != null)
        {
            val current = LocalDate.now()
            val from = meta.dateRange.from.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val to = meta.dateRange.to.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            isCurrentWeek = current.monthValue >= from.monthValue
                    && current.monthValue <= to.monthValue
                    && current.dayOfMonth >= from.dayOfMonth
                    && current.dayOfMonth <= to.dayOfMonth
        }

        return DaySelectionState(
            validDays = validDays,
            selectedDay = currentDay,
            isTodaySelected = isCurrentWeek && currentDay == LocalDate.now().dayOfWeek
        )
    }
}