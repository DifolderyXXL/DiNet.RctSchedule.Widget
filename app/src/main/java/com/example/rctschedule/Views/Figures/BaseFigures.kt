package com.example.rctschedule.Views.Figures

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun SurfaceText(text: String, modifier: GlanceModifier = GlanceModifier)
{
    Text(text,
        style = TextStyle(
            color = GlanceTheme.colors.onSurface),
        modifier = modifier)
}

@Composable
fun VerticalSpacer()
{
    Spacer(GlanceModifier.width(5.dp))
}

@Composable
fun HorizontalSpacer()
{
    Spacer(GlanceModifier.height(5.dp))
}
