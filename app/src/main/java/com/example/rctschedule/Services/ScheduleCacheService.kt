package com.example.rctschedule.Services

import android.util.Log
import com.example.rctschedule.Di.DatabaseDispatcher
import javax.inject.Inject
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.GroupExcelWeeksDTO
import com.example.rctschedule.dao.AppDatabase
import com.example.rctschedule.dao.GroupScheduleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ScheduleCacheService @Inject constructor(
    @DatabaseDispatcher private val dbDispatcher : CoroutineDispatcher,
    private val db: AppDatabase
) {
    suspend fun save(data: CacheEntry<GroupExcelWeeksDTO>)
    = withContext(dbDispatcher) {
        val entity = GroupScheduleEntity(
            groupId = data.data.group,
            updateTime = data.timestamp,
            weeksData = data.data
        )
        db.scheduleDao().upsertSchedule(entity)
    }

    suspend fun load(group: Int) : CacheEntry<GroupExcelWeeksDTO>? {
        var res : CacheEntry<GroupExcelWeeksDTO>? = null
        try {

            withContext(dbDispatcher)
            {
                val result = db.scheduleDao().loadById(group)

                if (!result.isEmpty()) {
                    val target = result.first()

                    res = CacheEntry(target.weeksData, target.updateTime)
                }
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return res
    }
}

