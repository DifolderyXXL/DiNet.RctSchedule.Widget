package com.example.rctschedule.Views.Callbacks

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.rctschedule.Model.WidgetEntry
import com.example.rctschedule.Services.Repositories.GroupToggleRepository
import com.example.rctschedule.Services.Repositories.ToggleData
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Views.SelectDayButtonType
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
            ep.getSelectDisplayUseCase()(selectedDayOfWeek = day)

            ep.getGroupToggleRepository().set(ToggleData.Default)
        }

        MyAppWidget().update(context, glanceId)
    }
}


suspend fun updateAndCloseMenu(context: Context, repo: GroupToggleRepository) {
    withContext(Dispatchers.Main) {
        repo.set(ToggleData.Default)
        MyAppWidget().updateAll(context)
    }
}

suspend fun closeMenu(repo: GroupToggleRepository) {
    withContext(Dispatchers.Main) {
        repo.set(ToggleData.Default)
    }
}