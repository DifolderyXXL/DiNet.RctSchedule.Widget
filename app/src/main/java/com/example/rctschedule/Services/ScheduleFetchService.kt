package com.example.rctschedule.Services

import com.example.rctschedule.Data.dto.GroupExcelWeeksDTO
import com.example.rctschedule.Di.IoDispatcher
import com.example.rctschedule.Repositories.exceptions.ScheduleDoesNotExists
import com.example.rctschedule.Services.Parsing.CourseParserProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleFetchService @Inject constructor(
    private val courseParserProvider: CourseParserProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchAsync(course: Int, group: Int) : Result<GroupExcelWeeksDTO>
    {
        return withContext(ioDispatcher)
        {
            try {
                val parser = courseParserProvider.get(course)

                val parsed = parser.get(group)
                if(parsed != null){
                    return@withContext Result.success(parsed)
                }

            } catch (e: Exception) {
                e.printStackTrace()

                return@withContext Result.failure(e)
            }

            Result.failure(ScheduleDoesNotExists())
        }
    }
}

