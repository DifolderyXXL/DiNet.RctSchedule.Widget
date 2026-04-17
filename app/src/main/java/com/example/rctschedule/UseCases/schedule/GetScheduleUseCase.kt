package com.example.rctschedule.UseCases.schedule

import com.example.rctschedule.Data.TransformService
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Repositories.FetchPolicy
import com.example.rctschedule.Repositories.ScheduleRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val transformService: TransformService
) {
    suspend operator fun invoke(course: Int, group: Int) : Result<ScheduleGroupWeeksData> {
        return scheduleRepository.getAsync(course, group, FetchPolicy.CacheFirst)
            .map {
                transformService.Transform(it)
            }
    }
}