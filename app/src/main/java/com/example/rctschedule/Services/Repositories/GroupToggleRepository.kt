package com.example.rctschedule.Services.Repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupToggleRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsRepository<Boolean>(
    context,
    SETTINGS_VALUE_NAME,
    Boolean::class.java,
    false,
    true){

    companion object{
        const val SETTINGS_VALUE_NAME = "group_toggle"
    }
}