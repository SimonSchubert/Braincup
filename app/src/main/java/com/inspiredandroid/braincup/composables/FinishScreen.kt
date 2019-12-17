package com.inspiredandroid.braincup.composables

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.app.AppController

fun FinishScreen(
    rank: String,
    newHighscore: Boolean,
    answeredAllCorrect: Boolean,
    plays: Int,
    random: () -> Unit,
    again: () -> Unit,
    gameMaster: AppController
) {
    BaseApp {
        Text(
            "Score: $rank",
            style = (+MaterialTheme.typography()).h4,
            modifier = Gravity.Center
        )
        HeightSpacer(16.dp)
        if (answeredAllCorrect) {
            Text(
                "You got 1 extra point for making zero mistakes.",
                style = (+MaterialTheme.typography()).h6,
                paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
                modifier = Gravity.Center
            )
        }
        if (newHighscore) {
            HeightSpacer(8.dp)
            Text(
                "New highscore",
                style = (+MaterialTheme.typography()).h6,
                modifier = Gravity.Center
            )
        }
        HeightSpacer(16.dp)
        Button("Play random game", onClick = {
            random()
        }, modifier = Gravity.Center)
        HeightSpacer(8.dp)
        Button("Play again", onClick = {
            again()
        }, modifier = Gravity.Center)
        HeightSpacer(8.dp)
        Button("Menu", onClick = {
            gameMaster.start()
        }, modifier = Gravity.Center)
    }
}
