package com.example.rctschedule.Views.Callbacks

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Services.Repositories.GroupToggleRepository
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Views.SelectDayButtonType
import com.example.rctschedule.Views.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
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
        val parameter = parameters[SELECT_DAY_BUTTON_KEY]

        val vm = WidgetModelRepository.get(context.applicationContext)
            .scheduleNavigationUseCase()

        Log.e(DaySelectActionCallback::class.simpleName, "onAction")

        when(parameter){
            /*SelectDayButtonType.NextDay ->{
                vm.nextDay()
            }

            SelectDayButtonType.PreviousDay ->{
                vm.previousDay()
            }

            SelectDayButtonType.CurrentDay ->{
                vm.currentDay()
            }*/

            SelectDayButtonType.ByIndex ->{
                Log.e(DaySelectActionCallback::class.simpleName, "ByIndex")

                withContext(Dispatchers.IO) {
                    val day = parameters[DAY_KEY]

                    if(day == null)
                    {
                        Log.e(DaySelectActionCallback::class.simpleName, "Day == null")
                        return@withContext
                    }

                    vm.selectDay(day)
                    Log.e(DaySelectActionCallback::class.simpleName, "Selected")

                    val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)

                    updateAndCloseMenu(context, ep.getGroupToggleRepository())
                }
            }

            else -> {
                Log.e(DaySelectActionCallback::class.simpleName, "else")

            }
        }

        //glanceId.updateAndCloseMenu(context)
    }
}


suspend fun updateAndCloseMenu(context: Context, repo: GroupToggleRepository) {
    withContext(Dispatchers.Main) {
        repo.set(false)
        MyAppWidget().updateAll(context)
    }
}

suspend fun closeMenu(repo: GroupToggleRepository) {
    withContext(Dispatchers.Main) {
        repo.set(false)
    }
}