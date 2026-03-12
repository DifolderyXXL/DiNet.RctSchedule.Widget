package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Views.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors

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


        val vm = WidgetModelRepository.get(context.applicationContext)
            .scheduleNavigationUseCase()

        vm.selectWeek(parameter)

        val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        updateAndCloseMenu(context, ep.getGroupToggleRepository())
    }
}