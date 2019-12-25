package com.inspiredandroid.braincup.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.*
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.challenge.ChallengeUrl
import com.inspiredandroid.braincup.challenge.ChallengeUrlError
import com.inspiredandroid.braincup.challenge.UrlBuilder

@Composable
fun CreateRiddleChallenge(
    context: Context,
    title: String,
    gameMaster: NavigationController
) {
    var challengeTitle = ""
    var secret = ""
    var quest = ""
    var answers = ""

    BaseScrollApp(title = title, back = { gameMaster.start(state = AppState.CREATE_CHALLENGE) }) {
        Subtitle1(
            text = "Create your own Riddle challenge.",
            modifier = Gravity.Center wraps Spacing(16.dp)
        )
        Column(modifier = Spacing(16.dp) wraps MaxWidth(300.dp)) {
            Input(
                title = "Title",
                helperText = "Title of the challenge. (optional)",
                modifier = Gravity.Center
            ) {
                challengeTitle = it
            }

            HeightSpacer(height = 16.dp)
            Input(
                title = "Secret",
                helperText = "The secret will be revealed after solving the challenge. (optional)",
                modifier = Gravity.Center
            ) {
                secret = it
            }

            HeightSpacer(height = 16.dp)
            Input(
                title = "Riddle",
                modifier = Gravity.Center
            ) {
                quest = it
            }

            HeightSpacer(height = 16.dp)
            Input(
                title = "Answers",
                helperText = "Separated by comma.",
                modifier = Gravity.Center
            ) {
                answers = it
            }
        }
        TextImageButton(
            text = "Create",
            drawableResource = R.drawable.ic_icons8_hammer,
            modifier = Gravity.Center wraps Spacing(16.dp),
            onClick = {
                val result = UrlBuilder.buildRiddleChallengeUrl(
                    challengeTitle,
                    secret,
                    quest,
                    answers
                )
                when (result) {
                    is ChallengeUrl -> {
                        shareText(context, result.url)
                    }
                    is ChallengeUrlError -> {
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}