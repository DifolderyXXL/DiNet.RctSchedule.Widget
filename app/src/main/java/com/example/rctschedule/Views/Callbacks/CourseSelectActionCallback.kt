package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Di.entryPoints.WidgetEntry
import com.example.rctschedule.ViewModels.Targeted.WidgetState
import com.example.rctschedule.Workers.InitializeWidgetWorker
import com.example.rctschedule.Workers.updateMyAppWidgetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseSelectActionCallback : ActionCallback {
    companion object{
        val SELECT_COURSE_BUTTON_KEY = ActionParameters.Key<Int>("SELECT_COURSE_BUTTON_KEY")
    }


    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val course = parameters[SELECT_COURSE_BUTTON_KEY] ?: return

        val ep = WidgetEntry.Companion.get(context.applicationContext)

        withContext(Dispatchers.IO)
        {
            val useCase = ep.getChangeCourseUseCase()

            useCase.changeCourse(course)
        }

        context.updateMyAppWidgetState(glanceId) {
            (it as? WidgetState.Content)?.copy(lastValidData = null)
                ?: it
        }

        InitializeWidgetWorker.Companion.enqueue(context.applicationContext)
    }
}