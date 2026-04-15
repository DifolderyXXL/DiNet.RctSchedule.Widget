package com.example.rctschedule.Views.WeekTableCalculation

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rctschedule.TransformExcelRow
import com.example.rctschedule.Views.Figures.mapToCalculatedCells
import com.example.rctschedule.Views.TableLayoutConfig

class ScheduleLayoutCalculator(private val context: Context){
    fun calculateRowLayout(
        row: TransformExcelRow,
        totalWidgetWidth: Dp,
        fontSize: Float
    ): CalculatedRow {
        val widgetWidth = totalWidgetWidth.value

        val fixedWidths = TableLayoutConfig.NumColumnWidth.value +
                TableLayoutConfig.TimeColumnWidth.value +
                TableLayoutConfig.RoomColumnWidth.value +
                TableLayoutConfig.WidgetHorizontalPadding.value +
                (TableLayoutConfig.ColumnSpacing.value * (row.columns.size - 1))

        val flexibleWidth = (widgetWidth - fixedWidths)
            .coerceAtLeast(1f)

        val colWidths = listOf(
            TableLayoutConfig.NumColumnWidth.value,
            TableLayoutConfig.TimeColumnWidth.value,
            flexibleWidth,
            TableLayoutConfig.RoomColumnWidth.value
        )
        val calculatedCols = row.columns.mapIndexed { index, column ->
            val colWidth = colWidths[index]
            CalculatedColumn(
                width = colWidth.dp,
                cells = mapToCalculatedCells(column, colWidth.toInt(), fontSize, context)
            )
        }

        val maxContentHeight = calculatedCols.maxOfOrNull {
            val cellsHeight = it.cells.sumOf { i -> i.height }
            val spacersHeight = if (it.cells.size > 1) (it.cells.size - 1) * TableLayoutConfig.CellRowSpacing.value.toInt() else 0
            cellsHeight + spacersHeight
        } ?: 0

        return CalculatedRow(
            (maxContentHeight.toFloat() + TableLayoutConfig.RowVerticalPadding.value).dp,
            calculatedCols
        )
    }
}