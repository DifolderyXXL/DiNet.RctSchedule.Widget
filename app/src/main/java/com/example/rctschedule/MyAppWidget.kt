package com.example.rctschedule

import android.content.Context
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
import com.example.rctschedule.Model.FetchState
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Model.WidgetViewModel
import com.example.rctschedule.Services.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        val vm = WidgetModelRepository.get(context).loadOrCreate()

        provideContent {
            GlanceTheme()
            {
                Column(GlanceModifier
                    .fillMaxHeight()
                    .background(GlanceTheme.colors.widgetBackground)
                    .appWidgetBackground()) {

                    Content(vm)
                }
            }
        }
    }

    @Composable
    private fun SurfaceText(text: String, modifier: GlanceModifier = GlanceModifier)
    {
        Text(text,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface),
            modifier = modifier)
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
    private fun UpdateStateHeader(viewModel: WidgetViewModel)
    {
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
    }

    @Composable
    private fun Header(viewModel: WidgetViewModel)
    {
        val updateState by viewModel.lastUpdate.collectAsState()
        SurfaceText("$updateState")
        UpdateStateHeader(viewModel)

        val metaState by viewModel.tableMetaData.collectAsState()
        Column(GlanceModifier.fillMaxWidth())
        {
            val formatter = SimpleDateFormat("dd MMM", Locale.US)

            SurfaceText("Week: ${metaState.weekNumber}")
            SurfaceText("Group: ${metaState.group}")
            SurfaceText(
                "Date: ${formatter.format(metaState.dateRange.from)}-${
                    formatter.format(
                        metaState.dateRange.to
                    )
                }"
            )
        }
    }

    @Composable
    private fun Body(viewModel: WidgetViewModel)
    {
        val state by viewModel.dayState.collectAsState(null)

        if(state != null) {
            Column {
                TableView(state!!)
                Spacer(modifier = GlanceModifier.height(20.dp))
            }
        }
        else{
            Text("<NULL>")
        }
    }

    @Composable
    private fun Content(viewModel: WidgetViewModel)
    {
        Column(GlanceModifier.padding(top = 5.dp)) {
            Header(viewModel)
            Body(viewModel)
        }
    }


    @Composable
    fun TableView(pr: TransformExcelTable)
    {
        LazyColumn{
            items(items = pr.rows){item ->
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

