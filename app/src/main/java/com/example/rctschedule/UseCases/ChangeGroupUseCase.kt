package com.example.rctschedule.UseCases

import com.example.rctschedule.Services.Parsing.ISheetRegularContextProvider
import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeGroupUseCase @Inject constructor(
    private val appSettingsRepository: ApplicationSettingsRepository,
    private val scheduleRepository: ScheduleDataRepository
) {

    suspend fun changeGroup(newGroupIndex: Int) {
        val bef = appSettingsRepository.get()

        appSettingsRepository.set(
            ApplicationSettings(bef.selectedCourse, newGroupIndex))

        scheduleRepository.loadSchedule(forceUpdate = false)
    }
}

@Singleton
class ChangeCourseUseCase @Inject constructor(
    private val appSettingsRepository: ApplicationSettingsRepository,
    private val scheduleRepository: ScheduleDataRepository,
    private val contextProvider: ISheetRegularContextProvider
) {

    suspend fun changeCourse(course: Int) {
        val bef = appSettingsRepository.get()

        val courses = contextProvider.getAllCourses()
        val selectedCourse = courses.firstOrNull{
            it == course
        }
            ?: courses.firstOrNull()
            ?: throw Exception("No courses provided")

        val context = contextProvider.get(selectedCourse)
        val group = if(bef.selectedGroup !in 0..<context.getGroupCount()) 0
            else bef.selectedGroup

        val settings = ApplicationSettings(selectedCourse, group)
        appSettingsRepository.set(settings)

        scheduleRepository.loadSchedule(forceUpdate = false)
    }
}


@Singleton
class GetAppSettingsUseCase @Inject constructor(
    private val appSettingsRepository: ApplicationSettingsRepository,
    private val contextProvider: ISheetRegularContextProvider
){
    suspend operator fun invoke(): ApplicationSettings{
        val bef = appSettingsRepository.get()

        val courses = contextProvider.getAllCourses()
        val selectedCourse = courses.firstOrNull{
            it == bef.selectedCourse
        }
            ?: courses.firstOrNull()
            ?: throw Exception("No courses provided")

        val context = contextProvider.get(selectedCourse)
        val group = if(bef.selectedGroup !in 0..<context.getGroupCount()) 0
            else bef.selectedGroup

        val settings = ApplicationSettings(selectedCourse, group)
        appSettingsRepository.set(settings)

        return settings
    }
}


