package com.example.rctschedule.Views.Figures

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.R
import androidx.glance.ImageProvider
import androidx.glance.background

@Composable
fun SurfaceText(text: String, modifier: GlanceModifier = GlanceModifier, fontSize: TextUnit? = null)
{
    Text(text,
        style = TextStyle(
            fontSize = fontSize,
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

private fun getColorTint(tintColor: ColorProvider? = null) : ColorFilter?{
    if(tintColor == null)
        return null

    return ColorFilter.tint(tintColor)
}

fun GlanceModifier.round10dpBackground(tintColor: ColorProvider? = null) : GlanceModifier{
    return this.background(ImageProvider(R.drawable.round10dp),
            colorFilter = getColorTint(tintColor))
}

fun GlanceModifier.round8dpBackground(tintColor: ColorProvider? = null) : GlanceModifier{
    return this.background(ImageProvider(R.drawable.round8dp),
        colorFilter = getColorTint(tintColor))
}
