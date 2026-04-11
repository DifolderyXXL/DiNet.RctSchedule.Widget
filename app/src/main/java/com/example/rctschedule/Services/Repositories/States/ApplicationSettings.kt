package com.example.rctschedule.Services.Repositories.States

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationSettings(
    val selectedCourse: Int,
    val selectedGroup: Int,
){
    constructor() : this(0, 0)

    companion object {
        val Default = ApplicationSettings()
    }
}

