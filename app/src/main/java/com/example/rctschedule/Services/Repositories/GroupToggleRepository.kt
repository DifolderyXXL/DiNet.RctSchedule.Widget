package com.example.rctschedule.Services.Repositories

import android.content.Context
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupToggleRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsRepository<Boolean>(
    context,
    SETTINGS_VALUE_NAME,
    createJsonSerializer(false)){

    companion object{
        const val SETTINGS_VALUE_NAME = "group_toggle"
    }
}