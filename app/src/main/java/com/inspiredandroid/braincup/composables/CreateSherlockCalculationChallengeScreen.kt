package com.inspiredandroid.braincup.composables

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.*
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.challenge.ChallengeUrl
import com.inspiredandroid.braincup.challenge.ChallengeUrlError
import com.inspiredandroid.braincup.challenge.UrlBuilder

@Composable
fun CreateSherlockCalculationChallenge(
    context: Context,
    title: String,
    gameMaster: NavigationController
) {
    var challengeTitle = ""
    var secret = ""
    var goal = ""
    var numbers = ""

    BaseScrollApp(title = title, back = {
        Handler().post {
            gameMaster.start(state = AppState.CREATE_CHALLENGE)
        }
        Unit
    }) {
        Subtitle1(
            text = "Create your own Sherlock Calculation challenge.",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally) + Modifier.padding(16.dp)
        )
        Column(
            modifier = Modifier.padding(16.dp) + Modifier.preferredWidthIn(maxWidth = 300.dp) + Modifier.gravity(
                align = Alignment.CenterHorizontally
            )
        ) {
            Input(
                title = "Title",
                helperText = "Title of the challenge. (optional)"
            ) {
                challengeTitle = it
            }

            Spacer(Modifier.preferredHeight(16.dp))
            Input(
                title = "Secret",
                helperText = "The secret will be revealed after solving the challenge. (optional)"
            ) {
                secret = it
            }

            Spacer(Modifier.preferredHeight(16.dp))
            Input(
                title = "Goal",
                helperText = "The goal that has to be found."
            ) {
                goal = it
            }

            Spacer(Modifier.preferredHeight(16.dp))
            Input(
                title = "Allowed numbers",
                helperText = "The allowed numbers to find the goal. (Separated by comma or space)"
            ) {
                numbers = it
            }
        }
        TextImageButton(
            text = "Create",
            drawableResource = R.drawable.ic_icons8_hammer,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally) + Modifier.padding(16.dp),
            onClick = {
                val result = UrlBuilder.buildSherlockCalculationChallengeUrl(
                    challengeTitle,
                    secret,
                    goal,
                    numbers
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