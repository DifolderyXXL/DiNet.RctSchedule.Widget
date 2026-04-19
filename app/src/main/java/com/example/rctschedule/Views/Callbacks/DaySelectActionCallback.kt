package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Di.entryPoints.WidgetEntry
import com.example.rctschedule.Views.SelectDayButtonType
import com.example.rctschedule.Workers.InitializeWidgetWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek

class DaySelectActionCallback : ActionCallback {
    companion object{
        val SELECT_DAY_BUTTON_KEY = ActionParameters.Key<SelectDayButtonType>("SELECT_DAY_BUTTON_KEY")
        val DAY_KEY = ActionParameters.Key<DayOfWeek>("DAY_KEY")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val ep = WidgetEntry.get(context.applicationContext)
        val day = parameters[DAY_KEY] ?: return

        withContext(Dispatchers.IO) {
            val appSettings = ep.getGetAppSettingsUseCase()()
            val schedule = ep.getGetScheduleUseCase()(appSettings.selectedCourse, appSettings.selectedGroup)

            schedule.onSuccess {
                ep.getSelectDisplayUseCase()(
                    schedule = it,
                    selectedDayOfWeek = day)
            }
        }

        InitializeWidgetWorker.enqueue(context.applicationContext)
    }
}