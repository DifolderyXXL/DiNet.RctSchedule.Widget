package com.example.rctschedule.ParserTesting

import com.example.rctschedule.Services.Parsing.GapSheetRegularContext
import com.example.rctschedule.Services.Parsing.RegularSheetParser
import com.example.rctschedule.Services.Parsing.SheetRegularContextProvider
import junit.framework.Assert.assertEquals
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupParserTest {
    private fun contextProvider() : SheetRegularContextProvider{
        val provider = SheetRegularContextProvider()

        provider.addContext(GapSheetRegularContext(
            listOf("N"), 1, 10, 5, 100
        ).specifyMetaGroupNameRow(4))

        provider.addContext(GapSheetRegularContext(
            listOf("N"), 2, 10, 5, 100
        ).specifyMetaGroupNameRow(4))

        provider.addContext(GapSheetRegularContext(
            listOf("N", "S"), 3, 9, 5, 100
        ).specifyMetaGroupNameRow(4))

        return provider
    }

    @Test
    fun `parse group 10 week 15 (issue with last columns)`() {
        val resource = javaClass.getResourceAsStream("/2course15week.xlsx")
        val wb = XSSFWorkbook(resource)
        val sheet = wb.getSheetAt(0)

        val parser = RegularSheetParser()

        val context = contextProvider().get(2)
        val result = parser.parse(sheet, 10, context)

        assertTrue(result.rows.any(){ it.size == result.totalCols })
        assertTrue(result.rows.size == result.totalRows)
    }
}
