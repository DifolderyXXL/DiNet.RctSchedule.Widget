package com.example.rctschedule

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object{
        const val UPDATE_WORKER_NAME = "update_schedule_worker"
        const val UPDATE_WORKER_REPEAT_INTERVAL_HOURS = 6L
        const val UPDATE_WORKER_FLEX_TIME_INTERVAL_MINUTES = 30L
    }

    private val workManager = WorkManager.getInstance(context)

    fun scheduleWidgetUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()

        val worker = PeriodicWorkRequestBuilder<UpdateScheduleWorker>(
            UPDATE_WORKER_REPEAT_INTERVAL_HOURS,
            TimeUnit.HOURS,
            UPDATE_WORKER_FLEX_TIME_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATE_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP ,
            worker
        )

        Log.d("WorkerScheduler", "Worker scheduled/updated")
    }

    fun observeWorkerStatus(callback: (WorkInfo?) -> Unit) {
        workManager.getWorkInfosForUniqueWorkLiveData(UPDATE_WORKER_NAME)
            .observeForever { workInfos ->
                callback(workInfos.firstOrNull())
            }
    }

    fun forceRunNow() {
        val oneTimeRequest = androidx.work.OneTimeWorkRequestBuilder<UpdateScheduleWorker>()
            .addTag("force_run")
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "force_update_worker",
            androidx.work.ExistingWorkPolicy.APPEND_OR_REPLACE,
            oneTimeRequest
        )

        Log.d("WorkerScheduler", "Forced worker run scheduled")
    }

    fun cancelWorker() {
        workManager.cancelUniqueWork(UPDATE_WORKER_NAME)
    }
}