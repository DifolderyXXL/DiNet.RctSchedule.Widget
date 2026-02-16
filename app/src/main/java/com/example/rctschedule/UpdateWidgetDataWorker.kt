package com.example.rctschedule

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Services.ExcelParser
import com.example.rctschedule.Services.ExcelTable
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.util.CellReference
import java.net.URL

class UpdateWidgetDataWorker : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val result = loadData()

        saveData(context, glanceId, result)

        Log.e("LOGSCHEDULE", "UPDATE call ${glanceId}")

        CoroutineScope(Dispatchers.Main).launch {
            MyAppWidget().update(context, glanceId)
        }
    }
}

suspend fun loadData(): ExcelTable? = withContext(Dispatchers.IO) {
    try {

        val url =
            URL("https://docs.google.com/spreadsheets/d/11LI8TxCfm8zyniVfH4gCaEzzgpTlSqHWeDob5sprBxw/export?format=xlsx")

        val rp = ExcelParser()

        val fromCol = CellReference.convertColStringToIndex("F")
        val fromColMeta = CellReference.convertColStringToIndex("B")


        var cols = rp.parseMultipleColumns(
            url.openStream(),
            0,
            100,
            2,
            listOf(fromColMeta, fromCol)
        )

        CombineTableColumns(cols)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> saveData(context: Context, glanceId: GlanceId, data: T?) {
    val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
    val json = Gson().toJson(data)
    prefs.edit().putString("widget_data_${glanceId}", json).apply()
}