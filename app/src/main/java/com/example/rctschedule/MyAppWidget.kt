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
import com.example.rctschedule.Model.DataState
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Model.WidgetViewModel
import com.example.rctschedule.Services.*
import com.google.gson.Gson
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject
import kotlin.jvm.java

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        val vm = WidgetModelRepository.get(context).loadOrCreate()

        provideContent {
            Content(context, vm)
        }
    }

    @Composable
    private fun Content(context: Context, viewModel: WidgetViewModel)
    {
        val state by viewModel.state.collectAsState(DataState.Null)

        when(state)
        {
            is DataState.Completed ->{
                val currentState =(state as DataState.Completed)
                Column {
                    Text("Last Update at ${currentState.updateTime}")
                    Button(
                        text = "Force",
                        onClick = {
                            viewModel.forceUpdateCommand()
                        }
                    )
                    Button(
                        text = "Lite",
                        onClick = {
                            viewModel.updateCommand()
                        }
                    )

                    TableView(currentState.table)
                }

            }
            else ->{
                Button(text = "Force load",
                    onClick = {
                        runBlocking {
                            viewModel.forceUpdateCommand()
                        }
                    })
            }
        }

    }


    @Composable
    fun TableView(pr: TransformExcelTable)
    {

        LazyColumn(modifier = GlanceModifier.fillMaxWidth())
        {
            items(items = pr.rows) { item ->
                Column {
                    RowView(item)
                    Spacer(modifier = GlanceModifier.height(8.dp))
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

