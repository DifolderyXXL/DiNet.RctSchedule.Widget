package com.example.rctschedule.Services.Repositories.States

import java.time.DayOfWeek

sealed class WidgetDisplayMode {
    object FollowCurrent : WidgetDisplayMode()

    data class Fixed(val weekId: Int, val dayOfWeek: DayOfWeek) : WidgetDisplayMode()
}