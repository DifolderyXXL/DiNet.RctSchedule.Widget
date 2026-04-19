package com.example.rctschedule.Services.Repositories.States

import kotlinx.serialization.Serializable

enum class ToggleWindow{
    Groups,
    Courses
}
@Serializable
data class ToggleData(val isExpanded: Boolean, val window: ToggleWindow){
    constructor() : this(false, ToggleWindow.Courses)
    companion object{
        val Default = ToggleData()
    }
}
