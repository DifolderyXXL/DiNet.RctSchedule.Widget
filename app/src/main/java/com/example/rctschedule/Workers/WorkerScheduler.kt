package com.example.rctschedule.Workers

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
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

    private val workManager by lazy { WorkManager.Companion.getInstance(context) }

    fun getWidgetUpdateWorkerStatus() : List<WorkInfo>
    {
        return workManager.getWorkInfosForUniqueWork(UPDATE_WORKER_NAME)
            .get()
    }

    fun scheduleWidgetUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
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
                WorkRequest.Companion.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            UPDATE_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<UpdateScheduleWorker>()
            .addTag("force_run")
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "force_update_worker",
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )

        Log.d("WorkerScheduler", "Forced worker run scheduled")
    }

    fun cancelWorker() {
        workManager.cancelUniqueWork(UPDATE_WORKER_NAME)
    }


    fun updateGroupOneTime(groupId: Int)
    {
        val workRequest = OneTimeWorkRequestBuilder<UpdateScheduleGroupWorker>()
            .setInputData(
                workDataOf(
                    UpdateScheduleGroupWorker.Companion.GROUP_ID_KEY to groupId
                )
            )
            .build()

        workManager.enqueueUniqueWork(
            "update_group_one_time",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}