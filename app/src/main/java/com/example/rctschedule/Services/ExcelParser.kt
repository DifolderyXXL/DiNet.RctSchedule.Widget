package com.example.rctschedule.Services

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

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

data class ExcelTableColumns(val columns: List<ExcelTable>){
    constructor() : this(emptyList())
}

class ExcelParser {
    fun parseMultipleColumns(inputStream: InputStream, startRow: Int, rowCount: Int, colCount: Int, columns: List<Int>)
    : ExcelTableColumns{

        val workbook = XSSFWorkbook(inputStream)

        val result = ArrayList<ExcelTable>()
        for(i in columns)
        {
            result.add(parseTable(workbook, startRow, i, rowCount, colCount))
        }

        return ExcelTableColumns(result);
    }

    fun parseTable(workbook: XSSFWorkbook, startRow: Int, startCol: Int, rowCount: Int, colCount: Int): ExcelTable {

        val sheet = workbook.getSheetAt(0)

        val mergedRegions = sheet.mergedRegions

        val tableData = mutableListOf<List<ExcelCell>>()

        for (r in 0 until rowCount) {
            val currentRow = sheet.getRow(startRow + r)
            val rowData = mutableListOf<ExcelCell>()

            for (c in 0 until colCount) {
                try{
                    val cell = currentRow.getCell(startCol + c)

                    val mergedRegion = findMergedRegion(mergedRegions, startRow + r, startCol + c)

                    if (mergedRegion != null) {
                        if (mergedRegion.firstRow <= startRow + r && mergedRegion.firstColumn <= startCol + c) {
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

        workbook.close()
        return ExcelTable(tableData, tableData.size, colCount)
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