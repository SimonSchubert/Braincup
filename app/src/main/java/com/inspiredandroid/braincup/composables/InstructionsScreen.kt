package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.Spacing
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

@Composable
fun InstructionsScreen(
    title: String,
    description: String,
    showChallengeInfo: Boolean,
    hasSecret: Boolean,
    start: () -> Unit,
    gameMaster: NavigationController
) {
    if(showChallengeInfo) {
        BaseScrollApp(title = title, back = { gameMaster.start() }) {
            Headline5(text = "You got challenged", modifier = Gravity.Center wraps Spacing(16.dp))
            if (hasSecret) {
                Subtitle2(text = "The challenge will unveil a secret.", modifier = Gravity.Center)
            }
            VectorImage(id = R.drawable.ic_message_sent, modifier = Gravity.Center)

            Subtitle1(text = description, modifier = Gravity.Center wraps Spacing(16.dp))
            Button("Start", onClick = {
                start()
            }, modifier = Gravity.Center)
        }
    } else {
        BaseApp(title = title, back = { gameMaster.start() }) {
            Subtitle1(text = description, modifier = Gravity.Center wraps Spacing(16.dp))
            Button("Start", onClick = {
                start()
            }, modifier = Gravity.Center)
        }
    }
}
