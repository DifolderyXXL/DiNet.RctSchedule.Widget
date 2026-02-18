package com.example.rctschedule

import com.example.rctschedule.Services.ExcelCell
import com.example.rctschedule.Services.ExcelTable
import com.example.rctschedule.Services.ExcelTableColumns
import kotlin.math.max

public fun CombineTableColumns(table: ExcelTableColumns)
        :ExcelTable
{
    val res = ArrayList<ArrayList<ExcelCell>>()

    val rowCount: Int = table.columns[0].totalRows
    var colCount: Int = 0

    for(i in table.columns)
        colCount += i.totalCols

    for(i in 0 until rowCount)
    {
        var row = ArrayList<ExcelCell>()

        for(t in table.columns)
        {
            for(r in t.rows[i])
            {
                row.add(r)
            }
        }

        res.add(row)
    }

    return ExcelTable(res, rowCount, colCount)
}

public fun TransformTable(table: ExcelTable) : TransformExcelTable
{
    val rows = ArrayList<TransformExcelRow>()
    var r = 0
    while(r < table.totalRows)
    {
        var maxH = 1
        for(c in 0 until table.totalCols)
        {
            val cell = table.rows[r][c]
            maxH = max(maxH, cell.rowSpan)
        }
        val cols = ArrayList<TransformExcelColumn>()

        var tm = 0
        var c = 0
        while(c < table.totalCols)
        {
            val col = ArrayList<ExcelCell>()

            var maxW = 1
            for(e in 0 until maxH - table.rows[r][c].rowSpan+1)
            {
                if(r+e >= table.totalRows)
                    break

                col.add(table.rows[r+e][c])
                maxW = max(maxW, table.rows[r+e][c].colSpan)
            }

            tm = max(tm, col.count())
            cols.add(TransformExcelColumn(maxW, col))
            c += maxW
        }

        rows.add(TransformExcelRow(maxH, cols))
        r+=maxH
    }

    return TransformExcelTable(rows)
}


data class TransformExcelTable(
    val rows: List<TransformExcelRow>
)

data class TransformExcelColumn(
    val width: Int,
    val rows: List<ExcelCell>
)

data class TransformExcelRow(
    val height: Int,
    val columns: List<TransformExcelColumn>
)