package com.example.rctschedule

import android.util.Log
import com.example.rctschedule.Services.ExcelParser
import com.example.rctschedule.Services.XLColumn
import org.apache.poi.ss.util.CellReference
import org.junit.Test

import org.junit.Assert.*
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun Er()
    {
        val url = URL("https://docs.google.com/spreadsheets/d/11LI8TxCfm8zyniVfH4gCaEzzgpTlSqHWeDob5sprBxw/export?format=xlsx")

        val rp = ExcelParser()

        val fromCol = CellReference.convertColStringToIndex("F")
        val toCol = CellReference.convertColStringToIndex("G")

        val r = rp.parseTable(url.openStream(), 0, fromCol, 30, 2)

        System.out.println("${r.totalCols}, ${r.totalRows}")
    }
}