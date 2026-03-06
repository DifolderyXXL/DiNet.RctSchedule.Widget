package com.example.rctschedule.Services

data class ApplicationSettings(
    val selectedGroup: Int
){
    constructor() : this(0)

    companion object {
        val Default = ApplicationSettings()
    }
}