package com.example.rctschedule

import android.util.Log
import com.example.rctschedule.Services.ExcelCell
import com.example.rctschedule.Services.ExcelTable
import com.example.rctschedule.Services.ExcelTableColumns
import kotlin.math.max

public fun CombineTableColumns(table: ExcelTableColumns, clearEmptyRows: Boolean)
        :ExcelTable
{
    val res = ArrayList<ArrayList<ExcelCell>>()


    for(i in 0 until table.columns[0].totalRows)
    {
        if(clearEmptyRows && checkRowIsEmpty(table, i))
            continue

        val row = ArrayList<ExcelCell>()

        for(t in table.columns)
        {
            for(r in t.rows[i])
            {
                row.add(r)
            }
        }

        res.add(row)
    }


    return ExcelTable(res, res.size, res[0].size)
}

public fun checkRowIsEmpty(table: ExcelTableColumns, row: Int)
    : Boolean
{
    for(t in table.columns)
    {
        for(r in t.rows[row])
        {
            if(r.value.isNotEmpty() && r.value.isNotBlank())
            {
                return false
            }
        }
    }

    return true
}


public fun TransformTable(inputRows: List<List<ExcelCell>>, totalRows: Int, totalCols: Int) : TransformExcelTable
{
    val rows = ArrayList<TransformExcelRow>()
    var r = 0
    while(r < totalRows)
    {
        var maxH = 1
        for(c in 0 until totalCols)
        {
            val cell = inputRows[r][c]
            maxH = max(maxH, cell.rowSpan)
        }
        val cols = ArrayList<TransformExcelColumn>()

        var tm = 0
        var c = 0
        while(c < totalCols)
        {
            val col = ArrayList<ExcelCell>()

            var maxW = 1
            for(e in 0 until maxH - inputRows[r][c].rowSpan+1)
            {
                if(r+e >= totalRows)
                    break

                col.add(inputRows[r+e][c])
                maxW = max(maxW, inputRows[r+e][c].colSpan)
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

public fun TransformWeek(table: ExcelTable, subjectCountingColumn: Int) : TransformExcelWeek
{
    var currentValue = 0f
    var prev = 0f
    var slidingStart = 0

    val days = ArrayList<TransformExcelTable>()

    for(i in 0 until table.rows.size+1)
    {
        if(i != table.rows.size) {
            prev = currentValue
            val v = table.rows[i][subjectCountingColumn].value
            val dt = v.toFloatOrNull()

            if (dt == null) {
                Log.e("E", "skip ${i} ${dt}")
                slidingStart = i
                continue
            }

            currentValue = dt
        }

        if(currentValue <= prev || i == table.rows.size)
        {
            val rows = i - slidingStart -1
            val target = table.rows.subList(slidingStart, i-1)
            days.add(TransformTable(target, rows, table.totalCols))

            Log.e("E", "${slidingStart}, ${i-1}, ${days.last().rows.size}, ${table.rows[0][0].value}")
            slidingStart = i

        }
    }

    return TransformExcelWeek(days)
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

data class TransformExcelWeek(
    val days: List<TransformExcelTable>
)