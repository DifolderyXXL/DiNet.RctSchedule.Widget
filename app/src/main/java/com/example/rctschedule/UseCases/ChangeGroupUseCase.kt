package com.example.rctschedule.UseCases

import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import com.example.rctschedule.WorkerScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeGroupUseCase @Inject constructor(
    private val appSettingsRepository: ApplicationSettingsRepository,
    private val scheduleRepository: ScheduleDataRepository,
) {

    suspend fun changeGroup(newGroupIndex: Int) {
        appSettingsRepository.set(ApplicationSettings(newGroupIndex))

        scheduleRepository.loadSchedule(forceUpdate = false)
    }
}


