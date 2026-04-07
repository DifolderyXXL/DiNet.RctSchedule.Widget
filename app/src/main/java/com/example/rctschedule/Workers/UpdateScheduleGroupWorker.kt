package com.example.rctschedule.Workers

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rctschedule.UseCases.ChangeGroupUseCase
import com.example.rctschedule.Views.MyAppWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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