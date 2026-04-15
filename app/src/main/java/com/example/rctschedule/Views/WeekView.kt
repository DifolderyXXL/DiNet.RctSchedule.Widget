package com.example.rctschedule.Views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import com.example.rctschedule.Data.ExcelCell
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.R
import com.example.rctschedule.ScheduleTheme.MyExcelAppTheme
import com.example.rctschedule.ScheduleTheme.ScheduleTheme
import com.example.rctschedule.TransformExcelRow
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.ViewModels.ContentState
import com.example.rctschedule.Views.Figures.SurfaceText
import com.example.rctschedule.Views.Figures.getLocalFontSize
import com.example.rctschedule.Views.WeekTableCalculation.CalculatedCell
import com.example.rctschedule.Views.WeekTableCalculation.ScheduleLayoutCalculator
import java.time.format.DateTimeFormatter

object TableLayoutConfig {
    val NumColumnWidth = 25.dp
    val TimeColumnWidth = 50.dp
    val RoomColumnWidth = 50.dp
    val ColumnSpacing = 4.dp
    val CellRowSpacing = 4.dp
    val RowVerticalPadding = 8.dp
    val WidgetHorizontalPadding = (2*4).dp
}

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
    fun RowView(c: TransformExcelRow) {
        val size = LocalSize.current
        val context = LocalContext.current
        val actualFontSize = getLocalFontSize()

        val layoutCalculator =remember { ScheduleLayoutCalculator(context)}
        val rowLayout =remember(c, size.width, actualFontSize) {
            layoutCalculator.calculateRowLayout(c, size.width, actualFontSize)
        }


        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(rowLayout.height)
        ) {
            rowLayout.columns.forEachIndexed { index, column ->
                ColumnView(
                    GlanceModifier.width(column.width).fillMaxHeight(),
                    index != 3,
                    actualFontSize,
                    column.cells)

                if (index < rowLayout.columns.size - 1) {
                    Spacer(modifier = GlanceModifier.width(TableLayoutConfig.ColumnSpacing))
                }
            }
        }
    }

    @Composable
    fun ColumnView(modifier: GlanceModifier, useEqualCellSizes: Boolean, fontSize: Float, message: List<CalculatedCell>){
        Column(modifier = modifier) {
            message.forEachIndexed { rowIndex, cell ->
                val isLast = rowIndex == message.size - 1

                val modifier = if(isLast || useEqualCellSizes)
                    GlanceModifier.defaultWeight()
                else GlanceModifier
                    .height(cell.height.dp)

                CellView(
                    c = cell.cell,
                    modifier = modifier.width(cell.width.dp),
                    fontSize = fontSize.sp
                )
                if (rowIndex < message.size - 1) {
                    Spacer(modifier = GlanceModifier.height(TableLayoutConfig.CellRowSpacing))
                }
            }
        }
    }

    @Composable
    fun CellView(c: ExcelCell, modifier: GlanceModifier = GlanceModifier,  fontSize: TextUnit? = null) {
        val background = ScheduleTheme.current.getMappedColor(
            c.rgb,
            GlanceTheme.colors.surfaceVariant
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SurfaceText(
                text = c.value,
                fontSize = fontSize
            )
        }
    }
}

