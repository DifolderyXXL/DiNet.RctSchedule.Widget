package com.example.rctschedule.Activities.navigation

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.example.rctschedule.Activities.pages.AboutPage
import com.example.rctschedule.Activities.pages.DebugPage
import com.example.rctschedule.Activities.pages.HomePage
import com.example.rctschedule.R

data class NavigationItem(
    val navKey: NavKey,
    @StringRes val labelResourceId: Int,
)

val NAVIGATION_ITEMS = listOf(
    NavigationItem(
        HomePage,
        R.string.home_page_title,
    ),
    NavigationItem(
        DebugPage,
        R.string.debug_page_title,
    ),
    NavigationItem(
        AboutPage,
        R.string.about_page_title,
    ),
)