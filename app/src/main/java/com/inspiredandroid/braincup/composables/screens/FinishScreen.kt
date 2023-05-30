package com.inspiredandroid.braincup.composables.screens

import android.os.Handler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.Headline4
import com.inspiredandroid.braincup.composables.Headline6
import com.inspiredandroid.braincup.composables.TextImageButton

@Composable
fun FinishScreen(
    rank: String,
    newHighscore: Boolean,
    answeredAllCorrect: Boolean,
    plays: Int,
    random: () -> Unit,
    again: () -> Unit,
    gameMaster: NavigationController
) {
    BaseApp {
        Headline4(
            text = "Score: $rank",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        if (answeredAllCorrect) {
            Text(
                "You got 1 extra point for making zero mistakes.",
                style = MaterialTheme.typography.h6.merge(ParagraphStyle(textAlign = TextAlign.Center)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        if (newHighscore) {
            Spacer(Modifier.height(8.dp))
            Headline6(
                text = "New highscore",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(Modifier.height(16.dp))
        TextImageButton(
            text = "Play random game",
            drawableResource = R.drawable.ic_icons8_dice,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                Handler().post {
                    random()
                }
            })
        Spacer(Modifier.height(16.dp))
        TextImageButton(
            text = "Play again",
            drawableResource = R.drawable.ic_icons8_recurring_appointment,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                Handler().post {
                    again()
                }
            })
        Spacer(Modifier.height(16.dp))
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                Handler().post {
                    gameMaster.start()
                }
            })
    }
}
