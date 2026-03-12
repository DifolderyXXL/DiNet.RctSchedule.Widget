package com.example.rctschedule

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.UseCases.ChangeGroupUseCase
import com.example.rctschedule.Views.MyAppWidget
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

            scheduleRepository.loadSchedule(true)

            MyAppWidget().updateAll(context)
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

@HiltWorker
class UpdateScheduleGroupWorker @AssistedInject constructor(
    val changeGroupUseCase: ChangeGroupUseCase,
    @Assisted val context: Context,
    @Assisted parameters: WorkerParameters
)
    : CoroutineWorker(context, parameters){

    companion object{
        const val GROUP_ID_KEY = "group_id"
    }
    override suspend fun doWork() : Result{
        try{
            Log.e(UpdateScheduleGroupWorker::class.simpleName, "Start")
            val groupId = inputData.getInt(GROUP_ID_KEY, -1)
            if (groupId == -1) return Result.failure()

            changeGroupUseCase.changeGroup(groupId)

            MyAppWidget().updateAll(context)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            return Result.retry()
        }

        return Result.success()
    }
}