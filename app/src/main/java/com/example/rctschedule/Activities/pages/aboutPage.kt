package com.example.rctschedule.Activities.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.rctschedule.R

data object AboutPage : NavKey

fun EntryProviderScope<NavKey>.aboutContentEntryBuilder(){
    entry<AboutPage>{
        AboutPageContent()
    }
}

@Composable
fun AboutPageContent(){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(Modifier.clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp)) {
            Text(stringResource(R.string.about_app),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(buildAnnotatedString {
                append(stringResource(R.string.connect_with_developer))
                append(" ")
                withLink(
                    LinkAnnotation.Url(
                        "https://t.me/RctWidgetScheduleSupportBot",
                        TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
                    )
                ) {
                    append("@RctWidgetScheduleSupportBot")
                }
                append(".")
            },
                color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}