package com.example.rctschedule.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import com.example.rctschedule.Model.GroupExcelWeeksDTO

@Entity(tableName = GroupScheduleEntity.tabelName, primaryKeys = ["group_id", "course_id"])
data class GroupScheduleEntity(
    @ColumnInfo(name = "group_id") val groupId: Int,
    @ColumnInfo(name = "course_id") val courseId: Int,
    @ColumnInfo(name = "update_time") val updateTime: Long,
    @ColumnInfo(name = "weeks_data") val weeksData: GroupExcelWeeksDTO
){
    companion object{
        const val tabelName = "group_schedules"
    }
}



@Dao
interface ScheduleDao {
    @Query("SELECT * FROM group_schedules")
    fun getAll(): List<GroupScheduleEntity>

    @Query("SELECT * FROM group_schedules WHERE course_id=:courseId AND group_id = :groupId")
    fun loadById(courseId: Int, groupId: Int): List<GroupScheduleEntity>

    @Query("SELECT * FROM group_schedules WHERE group_id IN (:groupIds) AND course_id IN (:courseIds)")
    fun loadAllByIds(courseIds: IntArray, groupIds: IntArray): List<GroupScheduleEntity>

    @Query("UPDATE group_schedules SET update_time=:updateTime, weeks_data = :weeksData WHERE course_id=:courseId AND group_id = :groupId")
    fun update(updateTime: Long, weeksData: GroupExcelWeeksDTO, courseId:Int, groupId: Int)

    @Upsert
    fun upsertSchedule(entity: GroupScheduleEntity)
}

