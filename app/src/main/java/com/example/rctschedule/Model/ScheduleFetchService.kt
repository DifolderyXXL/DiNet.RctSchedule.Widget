package com.example.rctschedule.Model

import com.example.rctschedule.CombineTableColumns
import com.example.rctschedule.Modules.IoDispatcher
import com.example.rctschedule.Services.ExcelParser
import com.example.rctschedule.Services.ExcelTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.apache.poi.ss.util.CellReference
import java.net.URL
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ScheduleFetchService @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchAsync() : ExcelTable?
    {
        var result: ExcelTable? = null
        withContext(ioDispatcher)
        {
            try {
                val url =
                    URL("https://docs.google.com/spreadsheets/d/11LI8TxCfm8zyniVfH4gCaEzzgpTlSqHWeDob5sprBxw/export?format=xlsx")

                val rp = ExcelParser()

                val fromCol = CellReference.convertColStringToIndex("F")
                val fromColMeta = CellReference.convertColStringToIndex("B")

                var cols = rp.parseMultipleColumns(
                    url.openStream(),
                    0,
                    100,
                    2,
                    listOf(fromColMeta, fromCol)
                )

                result = CombineTableColumns(cols)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return result
    }
}