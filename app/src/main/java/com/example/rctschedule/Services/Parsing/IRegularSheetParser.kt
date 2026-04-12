package com.example.rctschedule.Services.Parsing

import com.example.rctschedule.CombineTableColumns
import com.example.rctschedule.Data.ColumnArgument
import com.example.rctschedule.Data.ExcelCell
import com.example.rctschedule.Data.ExcelTable
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import javax.inject.Inject
import kotlin.math.min

interface IRegularSheetParser{
    fun parse(sheet: XSSFSheet, group: Int, regularContext: ISheetRegularContext)
        : ExcelTable
}

class RegularSheetParser @Inject constructor() : IRegularSheetParser{
    override fun parse(
        sheet: XSSFSheet,
        group: Int,
        regularContext: ISheetRegularContext
    ) : ExcelTable {

        val fromRow = regularContext.getFromRowIndex()
        val toRow = regularContext.getToRowIndex()

        val columns = mutableListOf(regularContext.getMetaColumnArgument())
        columns.add(regularContext.getContentForGroupColumn(group))

        val result = ArrayList<ExcelTable>()
        for(c in columns){
            result.add(takeColumn(sheet, fromRow, toRow, c))
        }

        val combined = CombineTableColumns(result, true)
        return combined
    }

    private fun takeColumn(
        sheet: XSSFSheet,
        fromRow: Int,
        toRow: Int,
        column: ColumnArgument) : ExcelTable
    {
        val mergedRegions = sheet.mergedRegions


        val colFrom = column.startCol
        val colTo = column.startCol + column.colCount - 1

        val emptyItem = ExcelCell("", 1, 1, true, null)

        val tableData = mutableListOf<List<ExcelCell>>()

        for(r in fromRow..min(toRow, sheet.lastRowNum))
        {
            val currentRow = sheet.getRow(r)
            val rowData = mutableListOf<ExcelCell>()


            for(c in colFrom..min(colTo, currentRow.lastCellNum.toInt()))
            {

                val cell = currentRow.getCell(c)
                if(cell == null)
                {
                    rowData.add(emptyItem)
                    continue
                }

                val mergedRegion = findMergedRegion(mergedRegions, r, c)

                val color = cell.cellStyle?.fillForegroundXSSFColor?.argbHex
                if (mergedRegion != null) {
                    if (mergedRegion.firstRow <= r && mergedRegion.firstColumn <= c) {
                        val value = getCellValue(cell)

                        val rowSpan = mergedRegion.lastRow - mergedRegion.firstRow + 1
                        val colSpan = mergedRegion.lastColumn - mergedRegion.firstColumn + 1

                        rowData.add(ExcelCell(value, rowSpan, colSpan, true, color))
                    } else {

                        rowData.add(emptyItem)
                    }
                } else {
                    rowData.add(ExcelCell(getCellValue(cell), 1, 1, false, color))
                }
            }

            if (rowData.isNotEmpty()) {
                tableData.add(rowData)
            }
        }

        return ExcelTable(
            tableData,
            tableData.size,
            tableData[0].size)
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
            CellType.STRING -> cell.stringCellValue.trimStart().trimEnd()
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