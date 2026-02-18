package com.example.rctschedule.Model

import android.content.Context
import android.util.Log
import com.example.rctschedule.Services.ExcelTable
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ScheduleCacheService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREFS_NAME = "schedule_widget_prefs"
    private val KEY_DATA = "excel_table_data"
    private val KEY_LAST_UPDATE = "last_update_time"

    fun save(data: ScheduleCacheData) {

        when(data)
        {
            is ScheduleCacheData.Ok ->{
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val json = Gson().toJson(data.table)

                Log.e("C", json.length.toString())

                with(prefs.edit())
                {
                    putString(KEY_DATA, json)
                    putLong(KEY_LAST_UPDATE, data.lastUpdateTime)
                    apply()
                }
            }
            else -> {

            }
        }
    }

    fun load() : ScheduleCacheData {
        try {
            val prefs = context
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_DATA, null)
            val lastUpdateTeme = prefs.getLong(KEY_LAST_UPDATE, 0)

            if (json != null)
            {
                val dr = Gson()
                        .fromJson(json, ExcelTable::class.java)

                if(dr != null)
                    return ScheduleCacheData.Ok(dr, lastUpdateTeme)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return ScheduleCacheData.None
    }
}