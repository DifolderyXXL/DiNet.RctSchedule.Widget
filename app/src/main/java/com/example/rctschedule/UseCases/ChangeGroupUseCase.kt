package com.example.rctschedule.UseCases

import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeGroupUseCase @Inject constructor(
    private val appSettingsRepository: ApplicationSettingsRepository,
) {

    suspend fun changeGroup(newGroupIndex: Int) {
        val bef = appSettingsRepository.get()

        appSettingsRepository.set(
            ApplicationSettings(bef.selectedCourse, newGroupIndex))
    }
}


