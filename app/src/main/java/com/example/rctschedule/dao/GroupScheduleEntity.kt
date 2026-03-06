package com.example.rctschedule.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import com.example.rctschedule.Model.GroupExcelWeeksDTO

@Entity(tableName = GroupScheduleEntity.tabelName)
data class GroupScheduleEntity(
    @PrimaryKey val groupId: Int,
    val updateTime: Long,
    val weeksData: GroupExcelWeeksDTO
){
    companion object{
        const val tabelName = "group_schedules"
    }
}
@Dao
interface ScheduleDao {
    @Query("SELECT * FROM group_schedules")
    fun getAll(): List<GroupScheduleEntity>

    @Query("SELECT * FROM group_schedules WHERE groupId = :id")
    fun loadById(id: Int): List<GroupScheduleEntity>

    @Query("SELECT * FROM group_schedules WHERE groupId IN (:groupIds)")
    fun loadAllByIds(groupIds: IntArray): List<GroupScheduleEntity>

    @Query("UPDATE group_schedules SET updateTime=:updateTime, weeksData = :weeksData WHERE groupId = :groupId")
    fun update(updateTime: Long, weeksData: GroupExcelWeeksDTO, groupId: Int)

    @Upsert
    fun upsertSchedule(entity: GroupScheduleEntity)
}

