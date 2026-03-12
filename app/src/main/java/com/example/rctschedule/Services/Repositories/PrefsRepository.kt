package com.example.rctschedule.Services.Repositories

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PrefsRepository<T: Any>(
    context: Context,
    private val contentPrefsName: String,
    private val classType: Class<T>,
    private val defaultValue: T,
    initialize: Boolean = true
){
    companion object{
        const val APPLICATION_PREFS_NAME = "ApplicationPrefs"
    }

    private val gson = Gson()
    private val prefs = context.getSharedPreferences(
        APPLICATION_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    val valueFlow : MutableStateFlow<T>

    init {
        val value = if(initialize) get() else defaultValue

        valueFlow = MutableStateFlow(onInit(value))
    }

    open fun onInit(value: T) : T{
        return value
    }


    fun get() : T {
        val value = prefs.getString(contentPrefsName, null)

        return if (value != null) {
            gson.fromJson(value, classType)
        } else {
            defaultValue
        }
    }

    fun set(value: T)
    {
        valueFlow.value = value

        prefs.edit {
            putString(contentPrefsName, gson.toJson(value))
        }
    }
}