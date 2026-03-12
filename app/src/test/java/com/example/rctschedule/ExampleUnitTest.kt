package com.example.rctschedule

import com.example.rctschedule.Services.ScheduleFetchService
import com.example.rctschedule.Data.TransformConfig
import com.example.rctschedule.Data.TransformService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat

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
    fun dateParsing()
    {
        val line = "23.02-28.02 (3-я неделя)"
        val regular = Regex("(?<fromDate>\\d*.\\d*)-(?<toDate>\\d*.\\d*)\\s\\((?<weekNumber>\\d).*\\)")
        val r = regular.find(line)

        r?.groups["fromDate"]
    }

    @Test
    fun dateTransform()
    {
        val line=  "03.02"
        val formatter = SimpleDateFormat("dd.MM")

        val date = formatter.parse(line)
    }

    @Test
    fun scheduleServiceTest() = runBlocking{
        val s = ScheduleFetchService(Dispatchers.IO)

        val result = s.fetchAsync(0)

        val ts = TransformService(TransformConfig(0))
        val tab = ts.Transform(result.getOrNull()!!)
            //Assert.assertTrue(tab.weekTable.days.size == 6)
    }
}