package com.example.rctschedule.Services

import com.example.rctschedule.Di.IoDispatcher
import com.example.rctschedule.Data.ColumnArgument
import com.example.rctschedule.Data.ExcelParser
import com.example.rctschedule.Data.ExcelSheetWeeks
import com.example.rctschedule.Model.GroupExcelTableDTO
import com.example.rctschedule.Model.GroupExcelWeeksDTO
import com.example.rctschedule.Model.ScheduleMeta
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

        var add = 0
        if(group > 5 -1)
            add = 1
        return ColumnArgument(
            groupDefaultColumnCount,
            index + group*groupDefaultColumnCount + add
        )
    }
}

class ScheduleFetchService @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchAsync(group: Int) : Result<GroupExcelWeeksDTO>
    {
        var result: GroupExcelWeeksDTO? = null

        try {
            withContext(ioDispatcher)
            {
                val url = URL(ExcelTableHelper.scheduleLink)

                val rp = ExcelParser()

                val weeks = rp.parseWorkbook(
                    url.openStream(),
                    5,
                    100,
                    listOf(
                        ExcelTableHelper.metaColumnArgument,
                        ExcelTableHelper.getGroupColumnArgument(group)
                    )
                )

                result = getWeeksDto(weeks, group)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            return Result.failure(e)
        }

        return Result.success(result!!)
    }

    private fun getWeeksDto(excelWeeks: ExcelSheetWeeks, group: Int) : GroupExcelWeeksDTO
    {
        val mapped = excelWeeks.weeks.map{e->
            GroupExcelTableDTO(
                e.week,
                ScheduleMeta(e.dateRange, e.weekNumber)
            )
        }

        return GroupExcelWeeksDTO(mapped, group)
    }
}

