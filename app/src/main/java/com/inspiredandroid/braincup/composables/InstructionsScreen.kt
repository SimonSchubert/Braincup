package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.Button
import androidx.ui.unit.dp
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
    if (showChallengeInfo) {
        BaseScrollApp(title = title, back = { gameMaster.start() }) {
            Headline5(
                text = "You got challenged", modifier = LayoutGravity.Center + LayoutPadding(
                    16.dp
                )
            )
            if (hasSecret) {
                Subtitle2(
                    text = "The challenge will unveil a secret.",
                    modifier = LayoutGravity.Center
                )
            }
            VectorImage(id = R.drawable.ic_message_sent, modifier = LayoutGravity.Center)

            Subtitle1(text = description, modifier = LayoutGravity.Center + LayoutPadding(16.dp))
            Button("Start", onClick = {
                start()
            }, modifier = LayoutGravity.Center)
        }
    } else {
        BaseApp(title = title, back = { gameMaster.start() }) {
            Subtitle1(text = description, modifier = LayoutGravity.Center + LayoutPadding(16.dp))
            Button("Start", onClick = {
                start()
            }, modifier = LayoutGravity.Center)
        }
    }
}
