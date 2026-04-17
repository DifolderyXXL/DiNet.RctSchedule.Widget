package com.example.rctschedule.Data

import com.example.rctschedule.Data.primitives.DateRange
import com.example.rctschedule.Model.ScheduleMeta

data class ExcelCell(
    val value: String = "",
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val isMerged: Boolean = false,
    val rgb: String? = null
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExcelCell

        if (rowSpan != other.rowSpan) return false
        if (colSpan != other.colSpan) return false
        if (isMerged != other.isMerged) return false
        if (value != other.value) return false
        if (!rgb.contentEquals(other.rgb)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rowSpan
        result = 31 * result + colSpan
        result = 31 * result + isMerged.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + (rgb?.hashCode() ?: 0)
        return result
    }
}

data class ExcelTable(
    val rows: List<List<ExcelCell>> = emptyList(),
    val totalRows: Int = 0,
    val totalCols: Int = 0
)

data class ColumnArgument(val colCount: Int, val startCol: Int)
