package com.example.rctschedule.Data

import com.example.rctschedule.CombineTableColumns
import com.example.rctschedule.Model.DateRange
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.text.SimpleDateFormat
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
    val dateRange: DateRange,
    val weekNumber: Int
                ){
    constructor() : this(ExcelTable(), DateRange(), 0)
}

data class ExcelSheetWeeks(
    val weeks: List<ExcelTableWeek>
)

data class ColumnArgument(val colCount: Int, val startCol: Int)

class ExcelParser {

    private val tableNameRegular = Regex("(?<fromDate>\\d*.\\d*)-(?<toDate>\\d*.\\d*)\\s*\\((?<weekNumber>\\d).*\\)")

    fun parseWorkbook(inputStream: InputStream, startRow: Int, rowCount: Int, columns: List<ColumnArgument>)
    : ExcelSheetWeeks{

        val workbook = XSSFWorkbook(inputStream)

        val weeks = arrayListOf<ExcelTableWeek>()
        for(i in 0 until workbook.numberOfSheets)
        {
            if(workbook.isSheetHidden(i) || workbook.isSheetVeryHidden(i))
                continue

            val sheet = workbook.getSheetAt(i)

            val table = parseSheet(sheet, startRow, rowCount, columns)
            weeks.add(table)
        }

        workbook.close()

        return ExcelSheetWeeks(weeks)
    }

    fun parseSheet(sheet: XSSFSheet, startRow: Int, rowCount: Int, columns: List<ColumnArgument>)
        : ExcelTableWeek
    {
        val r = tableNameRegular.find(sheet.sheetName)
            ?: throw Exception("Can't parse sheet name")
        val fromDate = r.groups["fromDate"]
        val toDate = r.groups["toDate"]
        val weekNumber = r.groups["weekNumber"]
        if(fromDate == null || toDate == null || weekNumber == null)
            throw Exception("Can't parse sheet name")

        val formatter = SimpleDateFormat("dd.MM")

        val dateRange = DateRange(
            formatter.parse(fromDate.value)!!,
            formatter.parse(toDate.value)!!)
        val weekNumberInt = weekNumber.value.toInt()

        val result = ArrayList<ExcelTable>()
        for(i in columns)
        {
            val de = parseTable(sheet, startRow, i.startCol, rowCount, i.colCount)
            result.add(de)
        }

        val combined = CombineTableColumns(result, true)
        return ExcelTableWeek(combined, dateRange, weekNumberInt)
    }

    fun parseTable(sheet: XSSFSheet, startRow: Int, startCol: Int, rowCount: Int, colCount: Int): ExcelTable {


        val mergedRegions = sheet.mergedRegions

        val tableData = mutableListOf<List<ExcelCell>>()

        for (r in startRow until min(startRow + rowCount, sheet.lastRowNum)) {
            val currentRow = sheet.getRow(r)
            val rowData = mutableListOf<ExcelCell>()

            for (c in startCol until min(startCol+colCount, currentRow.lastCellNum.toInt())) {
                try{
                    val cell = currentRow.getCell(c)

                    val mergedRegion = findMergedRegion(mergedRegions, r, c)

                    val color = cell.cellStyle?.fillForegroundXSSFColor?.rgb
                    if (mergedRegion != null) {
                        if (mergedRegion.firstRow <= r && mergedRegion.firstColumn <= c) {
                            val value = getCellValue(cell)
                            val rowSpan = mergedRegion.lastRow - mergedRegion.firstRow + 1
                            val colSpan = mergedRegion.lastColumn - mergedRegion.firstColumn + 1

                            rowData.add(ExcelCell(value, rowSpan, colSpan, true, color))
                        } else {

                            rowData.add(ExcelCell("", 1, 1, true, color))
                        }
                    } else {
                        rowData.add(ExcelCell(getCellValue(cell), 1, 1, false, color))
                    }
                }
                catch(e: Exception)
                {
                    e.printStackTrace()
                    rowData.add(ExcelCell("", 0, 0, true, null))
                }
            }
            if (rowData.isNotEmpty()) {
                tableData.add(rowData)
            }
        }

        return ExcelTable(tableData, tableData.size, tableData[0].size)
    }

    private fun findMergedRegion(mergedRegions: List<CellRangeAddress>, row: Int, col: Int): CellRangeAddress? {
        return mergedRegions.find { region ->
            row in region.firstRow..region.lastRow &&
                    col in region.firstColumn..region.lastColumn
        }
    }

    private fun getCellValue(cell: Cell?): String {
        if (cell == null) return ""

        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cell.dateCellValue.toString()
                } else {
                    cell.numericCellValue.toBigDecimal()
                        .stripTrailingZeros()
                        .toPlainString()
                }
            }
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> {
                try {
                    cell.stringCellValue
                } catch (e: Exception) {
                    cell.numericCellValue.toString()
                }
            }
            else -> ""
        }
    }
}