package com.example.rctschedule.Services.Repositories

import android.content.Context
import com.example.rctschedule.Services.Repositories.States.SelectedWeekSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedWeekRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : PrefsRepository<SelectedWeekSettings>(
    context,
    SETTINGS_VALUE_NAME,
    SelectedWeekSettings::class.java,
    SelectedWeekSettings.Default,
    true){

    companion object{
        const val SETTINGS_VALUE_NAME = "selected_week_settings"
    }
}