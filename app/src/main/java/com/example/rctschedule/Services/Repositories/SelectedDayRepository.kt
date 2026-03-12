package com.example.rctschedule.Services.Repositories

import android.content.Context
import com.example.rctschedule.Services.Repositories.States.DayFollowingType
import com.example.rctschedule.Services.Repositories.States.SelectedDaySettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedDayRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsRepository<SelectedDaySettings>(
    context,
    SETTINGS_VALUE_NAME,
    SelectedDaySettings::class.java,
    SelectedDaySettings.Default,
    true){

    companion object{
        const val SETTINGS_VALUE_NAME = "selected_day_settings"
    }
}