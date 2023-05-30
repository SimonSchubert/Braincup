package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        BaseScrollApp(title = title, back = {
            android.os.Handler().post {
                gameMaster.start()
            }
            Unit
        }) {
            Headline5(
                text = "You got challenged",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(
                    16.dp
                )
            )
            if (hasSecret) {
                Subtitle2(
                    text = "The challenge will unveil a secret.",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            VectorImage(
                id = R.drawable.ic_message_sent,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Subtitle1(
                text = description,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(
                    16.dp
                )
            )
            TextButton(text = "Start", onClick = {
                android.os.Handler().post {
                    start()
                }
            }, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    } else {
        BaseApp(title = title, back = {
            android.os.Handler().post {
                gameMaster.start()
            }
            Unit
        }) {
            Subtitle1(
                text = description,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(
                    16.dp
                )
            )
            TextButton(text = "Start", onClick = {
                android.os.Handler().post {
                    start()
                }
            }, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
