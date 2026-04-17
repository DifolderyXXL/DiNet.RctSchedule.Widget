package com.example.rctschedule

import com.example.rctschedule.Data.ExcelCell
import com.example.rctschedule.Data.ExcelTable
import kotlinx.serialization.Serializable
import kotlin.math.max


public fun CombineTableColumns(table: List<ExcelTable>, clearEmptyRows: Boolean)
        :ExcelTable
{
    val res = ArrayList<ArrayList<ExcelCell>>()


    for(i in 0 until table[0].totalRows)
    {
        if(clearEmptyRows && checkRowIsEmpty(table, i))
            continue

        val row = ArrayList<ExcelCell>()

        for(t in table)
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

public fun checkRowIsEmpty(table: List<ExcelTable>, row: Int)
    : Boolean
{
    for(t in table)
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


public fun TransformTable(inputRows: List<List<ExcelCell>>, totalRows: Int, totalCols: Int) : TransformExcelDayTable
{
    val rows = ArrayList<TransformExcelRow>()
    var r = 0
    while(r < totalRows)
    {
        var maxH = 1
        for(c in 0 until inputRows[r].size)
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

    return TransformExcelDayTable(rows)
}

public fun TransformWeek(table: ExcelTable, subjectCountingColumn: Int) : TransformExcelWeek
{
    var currentValue = 0f
    var prev = 0f
    var slidingStart = 0

    val days = ArrayList<TransformExcelDayTable>()

    for(i in 1 until table.rows.size+1)
    {
        if(i != table.rows.size) {
            prev = currentValue
            val v = table.rows[i][subjectCountingColumn].value
            val dt = v.toFloatOrNull()

            if (dt == null) {
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

            slidingStart = i
        }
    }

    return TransformExcelWeek(days)
}

@Serializable
data class TransformExcelDayTable(
    val rows: List<TransformExcelRow> = emptyList()
)

@Serializable
data class TransformExcelColumn(
    val width: Int,
    val rows: List<ExcelCell>
)

@Serializable
data class TransformExcelRow(
    val height: Int,
    val columns: List<TransformExcelColumn>
)

@Serializable
data class TransformExcelWeek(
    val days: List<TransformExcelDayTable>
)