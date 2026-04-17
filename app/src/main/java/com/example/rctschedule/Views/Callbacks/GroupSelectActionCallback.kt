package com.example.rctschedule.Views.Callbacks

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Di.entryPoints.WidgetEntry
import com.example.rctschedule.Views.MyAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupSelectActionCallback : ActionCallback {
    companion object{
        val SELECT_GROUP_BUTTON_KEY = ActionParameters.Key<Int>("SELECT_GROUP_BUTTON_KEY")
    }


    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val groupId = parameters[SELECT_GROUP_BUTTON_KEY] ?: return

        val ep = WidgetEntry.get(context.applicationContext)
        closeMenu(ep.getGroupToggleRepository())

        withContext(Dispatchers.IO)
        {
            val useCase = ep.getChangeGroupUseCase()

            useCase.changeGroup(groupId)

            ep.getScheduleDataRepository()
                .loadSchedule(forceUpdate = false)
        }

        MyAppWidget().update(context, glanceId)

        Log.e("GroupSelectActionCallback", "Action Completed")
    }
}

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

        val ep = WidgetEntry.get(context.applicationContext)
        closeMenu(ep.getGroupToggleRepository())

        withContext(Dispatchers.IO)
        {
            val useCase = ep.getChangeCourseUseCase()

            useCase.changeCourse(course)

            ep.getScheduleDataRepository()
                .loadSchedule(forceUpdate = false)
        }

        MyAppWidget().update(context, glanceId)

        Log.e("GroupSelectActionCallback", "Action Completed")
    }
}