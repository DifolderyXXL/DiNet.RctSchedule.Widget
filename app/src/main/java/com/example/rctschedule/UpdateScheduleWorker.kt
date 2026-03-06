package com.example.rctschedule

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rctschedule.Services.ScheduleDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateScheduleWorker @AssistedInject constructor(
    val scheduleRepository: ScheduleDataRepository,
    @Assisted val context: Context,
    @Assisted parameters: WorkerParameters
)
    : CoroutineWorker(context, parameters){

    override suspend fun doWork() : Result{
        try{
            Log.d("UpdateScheduleWorker", "Start worker")

            if (isStopped) {
                Log.d("UpdateScheduleWorker", "Worker stopped before starting")
                return Result.retry()
            }

            scheduleRepository.requestUpdate(true)

            MyAppWidget().updateAll(context)
        }
        catch (e: Exception)
        {
            Log.e("UpdateScheduleWorker", e.toString())
            return Result.retry()
        }

        return Result.success()
    }
}