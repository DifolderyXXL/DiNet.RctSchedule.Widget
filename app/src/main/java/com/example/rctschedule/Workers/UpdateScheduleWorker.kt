package com.example.rctschedule.Workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rctschedule.Repositories.ScheduleUpdater
import com.example.rctschedule.UseCases.GetAppSettingsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateScheduleWorker @AssistedInject constructor(
    val scheduleUpdater: ScheduleUpdater,
    val appSettingsUseCase: GetAppSettingsUseCase,
    @Assisted val context: Context,
    @Assisted parameters: WorkerParameters
) : CoroutineWorker(context, parameters){

    override suspend fun doWork() : Result{
        try{
            Log.d("UpdateScheduleWorker", "Start worker")

            if (isStopped) {
                Log.d("UpdateScheduleWorker", "Worker stopped before starting")
                return Result.retry()
            }

            val settings = appSettingsUseCase()
            scheduleUpdater.refreshFromServer(settings.selectedCourse, settings.selectedGroup)

            InitializeWidgetWorker.enqueue(context)
        }
        catch (e: Exception)
        {
            Log.e("UpdateScheduleWorker", e.toString())
            e.printStackTrace()
            return Result.retry()
        }

        return Result.success()
    }
}


