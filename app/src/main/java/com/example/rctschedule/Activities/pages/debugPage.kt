package com.example.rctschedule.Activities.pages

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.example.rctschedule.ScheduleAppWidgetReceiver
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Workers.WorkerScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.qualifiers.ApplicationContext

data object DebugPage : NavKey

fun EntryProviderScope<NavKey>.debugContentEntryBuilder(
    scheduler: WorkerScheduler,
    context: Context){
    entry<DebugPage>{
        val debugPageViewModel: DebugPageViewModel = viewModel{
            DebugPageViewModel(
                scheduler,
                context
            )
        }

        DebugPage(debugPageViewModel)
    }
}


class DebugPageViewModel(
    private var workerScheduler: WorkerScheduler,
    @ApplicationContext private val context: Context
) : ViewModel(){
    private val _debugText = MutableStateFlow<String?>(null)

    val debugText: StateFlow<String?> = _debugText

    fun updateWidgetCommand(){
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(context, ScheduleAppWidgetReceiver::class.java)
        )

        if (ids.isNotEmpty()) {
            val intent =
                Intent(context, ScheduleAppWidgetReceiver::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
            context.sendBroadcast(intent)
            Log.d(
                "TestActivity",
                "Обновление отправлено для ${ids.size} виджетов"
            )
        }
    }

    fun getFullWidgetUpdaterInfo(){
        val s = workerScheduler.getWidgetUpdateWorkerStatus()
        val info = s.joinToString { x -> x.toString() }

        _debugText.value = info;
    }

    fun getNextWidgetUpdateInfo(){
        val s = workerScheduler.getWidgetUpdateWorkerStatus()
        val info = s.joinToString { x ->
            val instant = Instant.ofEpochMilli(x.nextScheduleTimeMillis)

            val dateTime =
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            x.stopReason

            dateTime.format(formatter) + '\n'
        }

        _debugText.value = info
    }
}

@Composable
fun DebugPage(viewModel: DebugPageViewModel){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.Center) {
            Button(onClick = {
                viewModel.updateWidgetCommand()
            }) {
                Text("Update widgets")
            }

            Button(onClick = {
                viewModel.getFullWidgetUpdaterInfo()
            }) {
                Text("Get Full Info")
            }

            Button(onClick = {
                viewModel.getNextWidgetUpdateInfo()
            }) {
                Text("Get Next Schedule Info")
            }

            val debugText by viewModel.debugText.collectAsState()
            val text = remember(debugText) {
                if (debugText == null)
                    "<null>"
                else
                    debugText!!
            }

            Text(text)
        }
    }
}
