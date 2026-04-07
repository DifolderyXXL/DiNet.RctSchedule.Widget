package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.rctschedule.Model.WidgetEntry
import com.example.rctschedule.Views.MyAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToggleDropdownAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        val ep = WidgetEntry.get(context.applicationContext)
        val repo = ep.getGroupToggleRepository()

        withContext(Dispatchers.IO) {
            repo.set(!repo.get())
        }

        MyAppWidget().update(context, glanceId)
    }
}
