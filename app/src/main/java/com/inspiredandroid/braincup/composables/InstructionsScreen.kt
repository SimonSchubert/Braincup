package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.Spacing
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.app.AppController

@Composable
fun InstructionsScreen(
    title: String,
    description: String,
    start: () -> Unit,
    gameMaster: AppController
) {
    BaseApp(title = title, back = { gameMaster.start() }) {
        Text(
            description,
            style = (+MaterialTheme.typography()).subtitle1
            , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
            modifier = Gravity.Center wraps Spacing(16.dp)
        )
        Button("Start", onClick = {
            start()
        }, modifier = Gravity.Center)
    }
}
