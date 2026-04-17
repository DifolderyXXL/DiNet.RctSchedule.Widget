package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Di.entryPoints.WidgetEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeekSelectActionCallback : ActionCallback {
    companion object{
        val SELECT_WEEK_BUTTON_KEY = ActionParameters.Key<Int>("SELECT_WEEK_BUTTON_KEY")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val parameter = parameters[SELECT_WEEK_BUTTON_KEY]
            ?: return


        val ep = WidgetEntry.get(context.applicationContext)

        withContext(Dispatchers.IO) {
            ep.getSelectDisplayUseCase()(selectedWeekNumber = parameter)
        }

        updateAndCloseMenu(context, ep.getGroupToggleRepository())
    }
}