package com.example.rctschedule.Services.Repositories.States

import java.time.DayOfWeek

data class SelectedDaySettings(
    val selectedDayOfWeek: DayOfWeek,
    val followType: DayFollowingType
){
    constructor() : this(DayOfWeek.MONDAY, DayFollowingType.FollowToday)

    companion object {
        val Default = SelectedDaySettings()
    }
}