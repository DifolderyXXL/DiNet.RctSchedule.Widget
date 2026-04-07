package com.example.rctschedule.Services.Repositories.States

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationSettings(
    val selectedGroup: Int
){
    constructor() : this(0)

    companion object {
        val Default = ApplicationSettings()
    }
}

