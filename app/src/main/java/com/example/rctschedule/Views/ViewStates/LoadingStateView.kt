package com.example.rctschedule.Views.ViewStates

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.rctschedule.R
import com.example.rctschedule.Views.Figures.SurfaceText
import com.example.rctschedule.Views.Figures.round10dpBackground
import com.example.rctschedule.Workers.InitializeWidgetWorker

class InitializeScheduleAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        InitializeWidgetWorker.enqueue(context.applicationContext)
    }
}
@Composable
fun LoadingStateView(){
    Column(GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally) {
        val context = LocalContext.current
        SurfaceText(context.getString(R.string.loading))

        Text(context.getString(R.string.nothing_happens_button_text),
            style = TextStyle(
                color = GlanceTheme.colors.onPrimary),
            modifier = GlanceModifier
                .padding(5.dp, 2.dp)
                .round10dpBackground(GlanceTheme.colors.primary)
                .clickable(actionRunCallback<InitializeScheduleAction>()))
    }
}

