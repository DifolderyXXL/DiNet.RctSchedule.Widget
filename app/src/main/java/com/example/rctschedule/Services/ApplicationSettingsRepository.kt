package com.example.rctschedule.Services

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ApplicationSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
){
    private val gson = Gson()
    private val prefs = context.getSharedPreferences(
        PrefsName,
        Context.MODE_PRIVATE
    )

    val settingsFlow : MutableStateFlow<ApplicationSettings>

    init {
        settingsFlow = MutableStateFlow(get())
    }

    fun get() : ApplicationSettings {
        val value = prefs.getString(SettingsValueName, null)
        return if (value != null) {
            gson.fromJson(value, ApplicationSettings::class.java)
        } else {
            ApplicationSettings.Default
        }
    }

    fun set(value: ApplicationSettings)
    {
        settingsFlow.value = value

        val prefs = context.getSharedPreferences(
            PrefsName,
            Context.MODE_PRIVATE)

        prefs.edit {
            putString(SettingsValueName, gson.toJson(value))
        }
    }

    companion object{
        const val PrefsName = "ApplicationPrefs"
        const val SettingsValueName = "application_settings"
    }
}