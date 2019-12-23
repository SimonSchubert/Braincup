package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.Button
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
    BaseApp(title = title, back = { gameMaster.start() }) {
        if (showChallengeInfo) {
            Subtitle(text = "You got challenged", modifier = Gravity.Center)
            if (hasSecret) {
                HeightSpacer(height = 32.dp)
                // headline6("The challenge will unveil a secret.")
            }
            // illustration("message-sent.svg")
            // headline3(title)
        } else {
            Subtitle(text = description, modifier = Gravity.Center)
        }

        Button("Start", onClick = {
            start()
        }, modifier = Gravity.Center)
    }
}
