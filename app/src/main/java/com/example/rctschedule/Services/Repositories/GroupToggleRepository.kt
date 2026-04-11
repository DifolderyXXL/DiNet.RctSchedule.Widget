package com.example.rctschedule.Services.Repositories

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

enum class ToggleWindow{
    Groups,
    Courses
}
@Serializable
data class ToggleData(val isExpanded: Boolean, val window: ToggleWindow){
    constructor() : this(false, ToggleWindow.Courses)
    companion object{
        val Default = ToggleData()
    }
}

@Singleton
class GroupToggleRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsRepository<ToggleData>(
    context,
    SETTINGS_VALUE_NAME,
    createJsonSerializer(ToggleData.Default),
    corruptionHandler = ReplaceFileCorruptionHandler{ToggleData.Default}
){

    companion object{
        const val SETTINGS_VALUE_NAME = "group_toggle"
    }
}