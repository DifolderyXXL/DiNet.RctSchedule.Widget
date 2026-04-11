package com.example.rctschedule.Services.Repositories

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
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
    createJsonSerializer(ApplicationSettings.Default),
    ReplaceFileCorruptionHandler { ApplicationSettings.Default }
){

    companion object{
        const val SETTINGS_VALUE_NAME = "application_settings"
    }
}


