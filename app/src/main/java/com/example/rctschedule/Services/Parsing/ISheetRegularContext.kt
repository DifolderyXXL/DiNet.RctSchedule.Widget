package com.example.rctschedule.Services.Parsing

import com.example.rctschedule.Data.ColumnArgument
import org.apache.poi.ss.util.CellReference

interface ISheetRegularContext{
    fun getCourse(): Int
    fun getGroupCount(): Int

    fun getFromRowIndex(): Int
    fun getToRowIndex(): Int

    fun getMetaColumnArgument(): ColumnArgument
    fun getContentForGroupColumn(group: Int): ColumnArgument
}

class GapSheetRegularContext(
    gapColumns: List<String>,
    private val course: Int,
    private val groupCount: Int,
    private val fromRow: Int,
    private val toRow: Int
) : ISheetRegularContext{

    val metaColumnFromLetter = "B"
    val groupStartColumnLetter = "D"
    val groupDefaultColumnCount = 2

    val gapColumnInts: List<Int> = gapColumns.map {
        CellReference.convertColStringToIndex(it)
    }

    override fun getCourse(): Int {
        return course
    }

    override fun getGroupCount(): Int {
        return groupCount
    }

    override fun getFromRowIndex(): Int {
        return fromRow
    }

    override fun getToRowIndex(): Int {
        return toRow
    }

    override fun getMetaColumnArgument(): ColumnArgument {
        return ColumnArgument(
            2,
            CellReference.convertColStringToIndex(
                    metaColumnFromLetter))
    }

    override fun getContentForGroupColumn(group: Int): ColumnArgument {
        val index = CellReference.convertColStringToIndex(groupStartColumnLetter)

        var startCol = index + group* groupDefaultColumnCount
        for(i in gapColumnInts.sorted()){
            if(startCol >= i)
                startCol++
        }

        return ColumnArgument(
            groupDefaultColumnCount,
            startCol
        )
    }

}