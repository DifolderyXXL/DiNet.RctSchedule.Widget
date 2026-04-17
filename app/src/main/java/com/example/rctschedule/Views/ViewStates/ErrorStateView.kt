package com.example.rctschedule.Views.ViewStates

import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import com.example.rctschedule.R
import com.example.rctschedule.Views.Figures.SurfaceText

@Composable
fun ErrorStateView(error: String){
    val context = LocalContext.current
    SurfaceText(context.getString(R.string.error))
    SurfaceText("Message: ${error}")
}