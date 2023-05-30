package com.inspiredandroid.braincup.composables.screens

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.challenge.ChallengeUrl
import com.inspiredandroid.braincup.challenge.ChallengeUrlError
import com.inspiredandroid.braincup.challenge.UrlBuilder
import com.inspiredandroid.braincup.composables.BaseScrollApp
import com.inspiredandroid.braincup.composables.Input
import com.inspiredandroid.braincup.composables.Subtitle1
import com.inspiredandroid.braincup.composables.TextImageButton

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

    BaseScrollApp(title = title, back = {
        Handler().post {
            gameMaster.start(state = AppState.CREATE_CHALLENGE)
        }
        Unit
    }) {
        Subtitle1(
            text = "Create your own Riddle challenge.",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 300.dp)
                .align(
                    Alignment.CenterHorizontally
                )
        ) {
            Input(
                title = "Title",
                helperText = "Title of the challenge. (optional)"
            ) {
                challengeTitle = it
            }

            Spacer(Modifier.height(16.dp))
            Input(
                title = "Secret",
                helperText = "The secret will be revealed after solving the challenge. (optional)"
            ) {
                secret = it
            }

            Spacer(Modifier.height(16.dp))
            Input(
                title = "Riddle"
            ) {
                quest = it
            }

            Spacer(Modifier.height(16.dp))
            Input(
                title = "Answers",
                helperText = "Separated by comma."
            ) {
                answers = it
            }
        }
        TextImageButton(
            text = "Create",
            drawableResource = R.drawable.ic_icons8_hammer,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
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