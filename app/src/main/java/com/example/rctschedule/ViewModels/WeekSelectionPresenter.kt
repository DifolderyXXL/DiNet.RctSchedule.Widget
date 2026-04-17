package com.example.rctschedule.ViewModels

import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.ViewModels.Targeted.WeekSelectionViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeekSelectionPresenter @Inject constructor(){
    fun present(week: WeekSelectionViewModel): WeekSelectionState {

        return WeekSelectionState(
            availableWeeksIds = week.available,
            selectedWeekId = week.selected
        )
    }
}
