package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

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
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.preferredHeight(16.dp))
        if (answeredAllCorrect) {
            Text(
                "You got 1 extra point for making zero mistakes.",
                style = MaterialTheme.typography.h6.merge(ParagraphStyle(textAlign = TextAlign.Center)),
                modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
            )
        }
        if (newHighscore) {
            Spacer(Modifier.preferredHeight(8.dp))
            Headline6(
                text = "New highscore",
                modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
            )
        }
        Spacer(Modifier.preferredHeight(16.dp))
        TextImageButton(
            text = "Play random game",
            drawableResource = R.drawable.ic_icons8_dice,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally),
            onClick = {
                Handler().post {
                    random()
                }
            })
        Spacer(Modifier.preferredHeight(16.dp))
        TextImageButton(
            text = "Play again",
            drawableResource = R.drawable.ic_icons8_recurring_appointment,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally),
            onClick = {
                Handler().post {
                    again()
                }
            })
        Spacer(Modifier.preferredHeight(16.dp))
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally),
            onClick = {
                Handler().post {
                    gameMaster.start()
                }
            })
    }
}
