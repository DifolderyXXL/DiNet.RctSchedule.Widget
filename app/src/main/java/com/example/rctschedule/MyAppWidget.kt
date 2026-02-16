package com.example.rctschedule

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.Button
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Services.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.math.max

sealed interface State {

    object Loading : State
    object Error : State
    data class Completed(val table: TransformExcelTable) : State
}


class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        var result : TransformExcelTable? = null
        try {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val json = prefs.getString("widget_data_${id}", null)
            val dr = if (json != null) {
                Gson().fromJson(json, ExcelTable::class.java)
            } else null

            if(dr != null)
                result = TransformTable(dr)

            Log.e("LOGSCHEDULE", "Sub on ${id}; json ${json}")
        }
        catch (e: Exception){
            Log.e("LOGSCHEDULE", e.toString())
        }

        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val repository = WidgetModelRepository.get(context)

        provideContent {
            Content(context, widgetId, repository)

            /*Log.e("LOGSCHEDULE", "Produce")
            if (result == null) {
                MyContent(id)
            } else {
                TableView(context, result)
            }*/
        }
    }

    @Composable
    private fun Content(context: Context, widgetId: Int, repository: WidgetModelRepository)
    {
        val destinations by repository.loadOrCreate(widgetId).collectAsState(State.Loading)

        when (destinations) {
            is State.Loading -> {
                Button(text = "Update",
                    onClick = {
                        repository.updateModel(widgetId)
                    })
            }

            is State.Error -> {
                Text("Error")
            }

            is State.Completed -> {

                val table = (destinations as State.Completed).table
                TableView(context, table)
            }

            else -> {}
        }
    }


    @Composable
    private fun ZeroContent(id: GlanceId) {
        Log.e("LOGSCHEDULE", "Redraw")
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = "Home",
                    onClick = actionRunCallback<UpdateWidgetDataWorker>()
                )
            }
        }
    }


    @Composable
    fun TableView(context: Context, pr: TransformExcelTable)
    {
        Column(modifier = GlanceModifier.fillMaxWidth())
        {
            Button(
                text = "Home",
                onClick = actionRunCallback<UpdateWidgetDataWorker>()
            )
            Button(
                text = "update",
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        MyAppWidget().updateAll(context)
                    }
                }
            )


            LazyColumn()
            {
                items(items = pr.rows) { item ->
                    Column {
                        RowView(item)
                        Spacer(modifier = GlanceModifier.height(8.dp))
                    }
                }
            }
        }
    }



    @Composable
    fun RowView(c: TransformExcelRow)
    {
        Row(modifier = GlanceModifier.fillMaxWidth().wrapContentHeight().background(Color.Green))
        {
            var i = 0
            c.columns.forEach { message ->
                if(i == 2)
                    ColumnView(message, GlanceModifier.defaultWeight())
                else
                    ColumnView(message, GlanceModifier.width(50.dp))

                Spacer(modifier = GlanceModifier.width(8.dp))
                i++
            }
        }
    }

    @Composable
    fun ColumnView(c: TransformExcelColumn, modifier: GlanceModifier = GlanceModifier)
    {
        Column(modifier.fillMaxHeight()){
            c.rows.forEach { message ->
                CellView(message, GlanceModifier
                    .defaultWeight())
                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        }
    }

    @Composable
    fun CellView(c: ExcelCell, modifier: GlanceModifier = GlanceModifier)
    {
        Column(modifier
                .fillMaxWidth()
                .background(if (c.isMerged) Color.Red else Color.Gray)
        )
        {
            Text(text = c.value + "\n(${c.rowSpan}, ${c.colSpan})")
        }
    }

}

