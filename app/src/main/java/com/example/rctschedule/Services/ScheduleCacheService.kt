package com.example.rctschedule.Services

import com.example.rctschedule.Di.DatabaseDispatcher
import javax.inject.Inject
import com.example.rctschedule.Data.dto.ScheduleDTO
import com.example.rctschedule.Repositories.exceptions.ScheduleDoesNotExists
import com.example.rctschedule.dao.AppDatabase
import com.example.rctschedule.dao.GroupScheduleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ScheduleCacheService @Inject constructor(
    private val db: AppDatabase,
    @DatabaseDispatcher private val dbDispatcher : CoroutineDispatcher
) {
    suspend fun save(course:Int, group: Int, schedule: ScheduleDTO)
    = withContext(dbDispatcher) {
        val entity = GroupScheduleEntity(
            courseId = course,
            groupId = group,
            schedule = schedule
        )
        db.scheduleDao().upsertSchedule(entity)
    }

    suspend fun load(course: Int, group: Int) : Result<ScheduleDTO> {
        return withContext(dbDispatcher)
        {
            try {
                val result = db.scheduleDao().loadById(course, group)

                if (!result.isEmpty()) {
                    val target = result.first()

                    return@withContext Result.success(target.schedule)
                }
            }
            catch (e: Exception){
                e.printStackTrace()

                return@withContext Result.failure(e)
            }

            Result.failure(ScheduleDoesNotExists())
        }
    }
}

