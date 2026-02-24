package com.example.rctschedule.Views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import com.example.rctschedule.Model.WeekViewModel
import com.example.rctschedule.Services.ExcelCell
import com.example.rctschedule.SurfaceText
import com.example.rctschedule.TransformExcelColumn
import com.example.rctschedule.TransformExcelRow
import com.example.rctschedule.TransformExcelDayTable
import java.text.SimpleDateFormat
import java.util.Locale

interface GlanceView{
    @Composable
    fun ComposableDraw(modifier: GlanceModifier)
}

class WeekView(val viewModel: WeekViewModel) : GlanceView {

    @Composable
    override fun ComposableDraw(modifier: GlanceModifier) {
        Column(modifier)
        {
            Header()
            Body()
        }
    }

    @Composable
    private fun Body()
    {
        val state by viewModel.dayState.collectAsState(null)

        if(state != null) {
            Column {
                DaySelectionView(viewModel.daySelectionViewModel).ComposableDraw(GlanceModifier)
                TableView(state!!)
                Spacer(modifier = GlanceModifier.height(20.dp))
            }
        }
        else{
            Text("<NULL>")
        }
    }

    @Composable
    private fun Header()
    {
        val metaState by viewModel.tableMetaData.collectAsState()
        Column(GlanceModifier.fillMaxWidth())
        {
            val formatter = SimpleDateFormat("dd MMM", Locale.US)

            SurfaceText("Week: ${metaState.weekNumber}")
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
    fun TableView(pr: TransformExcelDayTable)
    {
        LazyColumn(GlanceModifier.background(Color.DarkGray)){
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
        Row(modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Green))
        {
            c.columns.forEachIndexed { index, message ->
                when (index) {
                    0 -> ColumnView(message, GlanceModifier.width(25.dp))
                    2 -> ColumnView(message, GlanceModifier.defaultWeight())
                    else -> ColumnView(message, GlanceModifier.width(50.dp))
                }

                Spacer(modifier = GlanceModifier.width(4.dp))
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

