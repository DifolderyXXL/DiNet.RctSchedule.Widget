package com.example.rctschedule.Views.Helpers

import android.content.Context

fun dpToPx(dp: Int, context: Context): Int {
    val density = context.resources.displayMetrics.density
    val totalWidth = (dp * density).toInt()
    return totalWidth
}