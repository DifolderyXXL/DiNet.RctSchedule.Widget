package com.example.rctschedule.Services.Repositories

import android.content.Context
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsRepository<ApplicationSettings>(
    context,
    SETTINGS_VALUE_NAME,
    ApplicationSettings::class.java,
    ApplicationSettings.Default,
    true){

    companion object{
        const val SETTINGS_VALUE_NAME = "application_settings"
    }
}


