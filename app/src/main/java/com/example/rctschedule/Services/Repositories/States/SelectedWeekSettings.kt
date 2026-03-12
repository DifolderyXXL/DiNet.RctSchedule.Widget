package com.example.rctschedule.Services.Repositories.States

data class SelectedWeekSettings(
    val selectedWeekNumber: Int,
){
    constructor() : this(-1)

    companion object {
        val Default = SelectedWeekSettings()
    }
}