package com.example.rctschedule

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory

import androidx.work.Configuration
import com.example.rctschedule.Workers.InitializeWidgetWorker
import com.example.rctschedule.Workers.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WidgetApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var scheduler: WorkerScheduler


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        InitializeWidgetWorker.enqueue(scheduler.workManager)

        val s = scheduler.getWidgetUpdateWorkerStatus()
        val info = s?.joinToString { x -> x.state.name }

        Log.e("app.onCreate", info ?: "null")

        scheduler.scheduleWidgetUpdate()

        Log.e("App", "START")
    }
}