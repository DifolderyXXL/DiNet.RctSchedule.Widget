package com.example.rctschedule.Services

import android.util.Log
import com.example.rctschedule.Model.DateRange
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import kotlin.math.min

data class ExcelCell(
    val value: String,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val isMerged: Boolean = false
)
{
    constructor() : this("", 1, 1, false)
}

data class ExcelTable(
    val rows: List<List<ExcelCell>>,
    val totalRows: Int,
    val totalCols: Int
){
    constructor() : this(emptyList(), 0, 0)
}


data class ExcelTableColumns(
    val columns: List<ExcelTable>,
    val dateRange: DateRange,
    val weekNumber: Int
                ){
    constructor() : this(emptyList(), DateRange(), 0)
}

data class ColumnArgument(val colCount: Int, val startCol: Int)

class ExcelParser {

    private val tableNameRegular = Regex("(?<fromDate>\\d*.\\d*)-(?<toDate>\\d*.\\d*)\\s\\((?<weekNumber>\\d).*\\)")



    fun parseMultipleColumns(inputStream: InputStream, startRow: Int, rowCount: Int, columns: List<ColumnArgument>)
    : ExcelTableColumns{

        val workbook = XSSFWorkbook(inputStream)

        val sheet = workbook.getSheetAt(0)

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
            result.add(parseTable(sheet, startRow, i.startCol, rowCount, i.colCount))
        }

        workbook.close()


        return ExcelTableColumns(result, dateRange, weekNumberInt)
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

                    if (mergedRegion != null) {
                        if (mergedRegion.firstRow <= r && mergedRegion.firstColumn <= c) {
                            val value = getCellValue(cell)
                            val rowSpan = mergedRegion.lastRow - mergedRegion.firstRow + 1
                            val colSpan = mergedRegion.lastColumn - mergedRegion.firstColumn + 1

                            rowData.add(ExcelCell(value, rowSpan, colSpan, true))
                        } else {

                            rowData.add(ExcelCell("", 1, 1, true))
                        }
                    } else {
                        rowData.add(ExcelCell(getCellValue(cell), 1, 1, false))
                    }
                }
                catch(e: Exception)
                {
                    rowData.add(ExcelCell("", 0, 0, true))
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
                    cell.numericCellValue.toString()
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