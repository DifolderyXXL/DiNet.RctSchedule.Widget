package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.rctschedule.Model.WidgetEntry
import com.example.rctschedule.Services.Repositories.ToggleData
import com.example.rctschedule.Services.Repositories.ToggleWindow
import com.example.rctschedule.Views.Callbacks.CourseSelectActionCallback.Companion.SELECT_COURSE_BUTTON_KEY
import com.example.rctschedule.Views.MyAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToggleDropdownAction : ActionCallback {
    companion object{
        val WINDOW_TOGGLE_KEY = ActionParameters.Key<ToggleWindow>("WINDOW_TOGGLE_KEY")
    }


    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val window = parameters[WINDOW_TOGGLE_KEY] ?: return

        val ep = WidgetEntry.get(context.applicationContext)
        val repo = ep.getGroupToggleRepository()

        withContext(Dispatchers.IO) {
            val was = repo.get()
            val expanded = if(!was.isExpanded || was.window == window) !was.isExpanded else true
            repo.set(ToggleData(expanded, window))
        }

        MyAppWidget().update(context, glanceId)
    }
}
