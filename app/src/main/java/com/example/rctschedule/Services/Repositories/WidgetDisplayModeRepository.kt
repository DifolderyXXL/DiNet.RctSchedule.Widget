package com.example.rctschedule.Services.Repositories

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetDisplayModeRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TYPE_KEY = stringPreferencesKey("TypePropertyName")
    private val WEEK_KEY = intPreferencesKey("WeekIdPropertyName")
    private val DAY_KEY = intPreferencesKey("DayPropertyName")

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "APPLICATION_PREFS_NAME")


    val valueFlow: Flow<WidgetDisplayMode> = context.dataStore.data
        .map { prefs ->
            val type = prefs[TYPE_KEY] ?: "FollowCurrent"
            if (type == "Fixed") {
                WidgetDisplayMode.Fixed(
                    prefs[WEEK_KEY] ?: 0,
                    DayOfWeek.of(prefs[DAY_KEY] ?: 1)
                )
            } else {
                WidgetDisplayMode.FollowCurrent
            }
        }.distinctUntilChanged()

    suspend fun get() : WidgetDisplayMode{
        return valueFlow.first()
    }

    suspend fun set(value: WidgetDisplayMode) {
        context.dataStore.edit { prefs ->
            when (value) {
                is WidgetDisplayMode.FollowCurrent -> {
                    prefs[TYPE_KEY] = "FollowCurrent"
                }

                is WidgetDisplayMode.Fixed -> {
                    prefs[TYPE_KEY] = "Fixed"
                    prefs[WEEK_KEY] = value.weekId
                    prefs[DAY_KEY] = value.dayOfWeek.value
                }
            }


        }
    }
}