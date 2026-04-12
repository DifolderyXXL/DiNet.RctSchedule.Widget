package com.example.rctschedule.Services.Parsing

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.net.URL
import javax.inject.Inject

interface IWebApi {
    fun provideSheetForCourse(course: Int) : XSSFWorkbook
}

class RctWebApi @Inject constructor() : IWebApi{
    val excelTabId = mapOf(
        1 to "1Wmsij8rOJAcOaPaKWnUphEghdldCRvXDqvX7am6Km4A",
        2 to "11LI8TxCfm8zyniVfH4gCaEzzgpTlSqHWeDob5sprBxw",
        3 to "1itE56-6GQvK2MvNBBtos2O7sGFGD0pC7zpWM4CV2OnU"
    )

    override fun provideSheetForCourse(course: Int): XSSFWorkbook {
        val id = excelTabId[course]
            ?: throw Exception("Id for $course course not found")

        val url = URL(getLink(id))
        return XSSFWorkbook(url.openStream())
    }

    private fun getLink(id: String) : String
    {
        return "https://docs.google.com/spreadsheets/d/$id/export?format=xlsx"
    }
}

