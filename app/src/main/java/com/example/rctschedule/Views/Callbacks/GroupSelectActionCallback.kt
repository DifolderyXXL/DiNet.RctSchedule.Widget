package com.example.rctschedule.Views.Callbacks

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import com.example.rctschedule.Views.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors

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



        // 2. Получаем EntryPoint
        val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)

        // 1. Закрываем меню (через ваши Preferences)
        closeMenu(ep.getGroupToggleRepository())

        // 3. МЕНЯЕМ ГРУППУ ПРЯМО ЗДЕСЬ (в фоне SessionWorker)
        // Это быстро: просто запись в DataStore/Prefs
        ep.getAppSettingsRepository().set(ApplicationSettings(groupId))

        // 4. Опционально: запускаем загрузку данных в фоне,
        // но НЕ через Scheduler, а просто вызвав UseCase
        // Если loadSchedule(true) делает сетевой запрос, SessionWorker подождет его.
        ep.getScheduleDataRepository().loadSchedule(forceUpdate = false)

        Log.e("GroupSelectActionCallback", "Action Completed")
    }
}