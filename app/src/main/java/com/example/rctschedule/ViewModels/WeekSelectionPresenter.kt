package com.example.rctschedule.ViewModels

import com.example.rctschedule.Model.ScheduleGroupWeeksData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeekSelectionPresenter @Inject constructor(){
    fun present(schedule: ScheduleUiState, currentWeek: Int): WeekSelectionState {
        val weeks = schedule.selectWeek.weeksMetas

        return WeekSelectionState(
            availableWeeksIds = weeks,
            selectedWeekId = currentWeek
        )
    }
}
