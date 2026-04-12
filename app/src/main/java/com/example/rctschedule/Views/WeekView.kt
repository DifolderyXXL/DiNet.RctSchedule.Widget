package com.example.rctschedule.Views

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.Data.ExcelCell
import com.example.rctschedule.Model.ScheduleDayData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.R
import com.example.rctschedule.ScheduleTheme.MyExcelAppTheme
import com.example.rctschedule.ScheduleTheme.ScheduleTheme
import com.example.rctschedule.TransformExcelColumn
import com.example.rctschedule.TransformExcelRow
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.ViewModels.ContentState
import com.example.rctschedule.ViewModels.ScheduleUiState
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

interface GlanceView{
    @Composable
    fun ComposableDraw(modifier: GlanceModifier)
}

class WeekView(val state: ContentState) : GlanceView {

    @Composable
    override fun ComposableDraw(modifier: GlanceModifier) {

        Column(modifier)
        {
            if(state.displayData != null) {
                Header(state.displayData.week.meta, state.course, state.group)
                TableView(state.displayData.day.weekTable)
            }

            Spacer(modifier = GlanceModifier.height(20.dp))
        }
    }

    @Composable
    private fun Header(metaState: ScheduleMeta, course: Int, group: Int)
    {
        Row(GlanceModifier.fillMaxWidth())
        {
            val formatter = DateTimeFormatter.ofPattern("dd MMM")

            val context = LocalContext.current

            SurfaceText(
                "${formatter.format(metaState.dateRange.from)}-${
                    formatter.format(
                        metaState.dateRange.to
                    )
                }(${metaState.weekNumber}-${context.getString(R.string.week)}) " +
                        "(${context.getString(R.string.course)} ${course}) " +
                        "(${context.getString(R.string.group)} ${group+1})")

        }
    }

    @Composable
    fun TableView(pr: TransformExcelDayTable)
    {
        MyExcelAppTheme{
            LazyColumn(GlanceModifier
                .background(GlanceTheme.colors.background)
                .cornerRadius(8.dp)){
                items(items = pr.rows){item ->
                    Column {
                        RowView(item)
                        Spacer(modifier = GlanceModifier.height(4.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun RowView(c: TransformExcelRow)
    {
        Row(modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight())
        {
            c.columns.forEachIndexed { index, message ->
                when (index) {
                    0 -> ColumnView(message, GlanceModifier.width(25.dp))
                    2 -> ColumnView(message, GlanceModifier.defaultWeight())
                    else -> ColumnView(message, GlanceModifier.width(50.dp))
                }

                if(index < c.columns.size-1)
                    Spacer(modifier = GlanceModifier.width(4.dp))
            }
        }
    }

    @Composable
    fun ColumnView(c: TransformExcelColumn, modifier: GlanceModifier = GlanceModifier)
    {
        Column(modifier.fillMaxHeight()){
            c.rows.forEachIndexed { index, message ->
                CellView(message, GlanceModifier
                    .defaultWeight())


                if(index < c.rows.size-1)
                    Spacer(modifier = GlanceModifier.height(4.dp))
            }
        }
    }

    @Composable
    fun CellView(c: ExcelCell, modifier: GlanceModifier = GlanceModifier)
    {
        val background =
            ScheduleTheme.current.getMappedColor(
                c.rgb,
                GlanceTheme.colors.surfaceVariant)


        Column(modifier
            .fillMaxWidth()
            .padding(2.dp)
            .background(background)
        )
        {
            //SurfaceText(text = "${c.isMerged}, ${c.colSpan}, ${c.rowSpan}")
            SurfaceText(text = c.value)
        }
    }

    fun parseExcelHexToComposeColor(hex: String): Color {
        val colorLong = hex.removePrefix("#").toLong(16)
        return Color(colorLong)
    }
}

