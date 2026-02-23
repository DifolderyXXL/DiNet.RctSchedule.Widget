package com.example.rctschedule.Model

import com.example.rctschedule.CombineTableColumns
import com.example.rctschedule.Modules.IoDispatcher
import com.example.rctschedule.Services.ColumnArgument
import com.example.rctschedule.Services.ExcelParser
import com.example.rctschedule.Services.ExcelTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.apache.poi.ss.util.CellReference
import java.net.URL
import javax.inject.Inject

object ExcelTableHelper
{
    val scheduleLink = "https://docs.google.com/spreadsheets/d/11LI8TxCfm8zyniVfH4gCaEzzgpTlSqHWeDob5sprBxw/export?format=xlsx"
    val metaColumnFromLetter = "B"
    val groupStartColumnLetter = "D"
    val groupDefaultColumnCount = 2

    val metaColumnArgument : ColumnArgument
        get() {
            return ColumnArgument(
                2,
                CellReference.convertColStringToIndex(metaColumnFromLetter))
        }

    fun getGroupColumnArgument(group: Int) : ColumnArgument{
        val index = CellReference.convertColStringToIndex(groupStartColumnLetter)

        return ColumnArgument(
            groupDefaultColumnCount,
            index + group*groupDefaultColumnCount
        )
    }
}

class ScheduleFetchService @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchAsync(group: Int) : Result<GroupExcelTableDTO>
    {
        var result: ExcelTable? = null
        var dateRange: DateRange? = null
        var weekNumber: Int = 0

        try {
            withContext(ioDispatcher)
            {
                val url = URL(ExcelTableHelper.scheduleLink)

                val rp = ExcelParser()

                val cols = rp.parseMultipleColumns(
                    url.openStream(),
                    5,
                    100,
                    listOf(
                        ExcelTableHelper.metaColumnArgument,
                        ExcelTableHelper.getGroupColumnArgument(group)
                    )
                )

                dateRange = cols.dateRange
                weekNumber = cols.weekNumber

                result = CombineTableColumns(cols, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            return Result.failure(e)
        }

        return Result.success(
            GroupExcelTableDTO(result!!, ExcelTableMetaData(group, dateRange!!, weekNumber)))
    }
}