package com.example.rctschedule.Data

import android.util.Log
import com.example.rctschedule.CombineTableColumns
import com.example.rctschedule.Model.DateRange
import com.example.rctschedule.Model.ScheduleMeta
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.text.get

data class ExcelCell(
    val value: String,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val isMerged: Boolean = false,
    val rgb: ByteArray?
)
{
    constructor() : this("", 1, 1, false, null)

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
        result = 31 * result + (rgb?.contentHashCode() ?: 0)
        return result
    }
}

data class ExcelTable(
    val rows: List<List<ExcelCell>>,
    val totalRows: Int,
    val totalCols: Int
){
    constructor() : this(emptyList(), 0, 0)
}


data class ExcelTableWeek(
    val week: ExcelTable,
    val meta: ScheduleMeta
                ){
    constructor() : this(ExcelTable(), ScheduleMeta(DateRange(), 0))
}

data class ExcelSheetWeeks(
    val weeks: List<ExcelTableWeek>
)

data class ColumnArgument(val colCount: Int, val startCol: Int)
