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
import androidx.glance.action.clickable
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.text.TextStyle
import com.example.rctschedule.Model.DataState
import com.example.rctschedule.Model.FetchState
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Model.WidgetViewModel
import com.example.rctschedule.Services.*
import kotlinx.coroutines.runBlocking

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        Log.e("E", "E")

        val vm = WidgetModelRepository.get(context).loadOrCreate()

        provideContent {


            Content(context, vm)
        }
    }


    @Composable
    private fun FetchingStateContent(viewModel: WidgetViewModel)
    {
        val state by viewModel.fetchState.collectAsState(FetchState.Null)

        Box {
            when (state) {
                is FetchState.Null -> {
                    Text("Null",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface
                        )
                    )
                }

                is FetchState.Error -> {
                    Text("Error")
                }

                is FetchState.Fetching -> {
                    Text("Fetching")
                }

                is FetchState.Completed -> {
                    Text("Completed")
                }
            }
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
                Column(GlanceModifier.padding(top=10.dp)) {
                    Text("${currentState.updateTime}")
                    Row(GlanceModifier.fillMaxWidth()){
                        FetchingStateContent(viewModel)
                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Image(
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                            provider = ImageProvider(R.drawable.baseline_refresh_24),
                            contentDescription = null,
                            modifier = GlanceModifier.cornerRadius(5.dp).clickable{
                                viewModel.forceUpdateCommand()
                            }
                        )

                    }

                    LazyColumn(modifier = GlanceModifier.fillMaxWidth())
                    {
                        items(items = currentState.table.days) { item ->
                            Column{

                                TableView(item)
                                Spacer(modifier = GlanceModifier.height(20.dp))
                            }
                        }
                    }
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
        Column{
            pr.rows.forEach { item ->
                Column {
                    RowView(item)
                    Spacer(modifier = GlanceModifier.height(4.dp))
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

                Spacer(modifier = GlanceModifier.width(4.dp))
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
                Spacer(modifier = GlanceModifier.height(4.dp))
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
            Text(text = c.value)
        }
    }

}

