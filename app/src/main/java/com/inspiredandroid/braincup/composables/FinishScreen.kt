package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
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
        Headline4(text = "Score: $rank", modifier = Gravity.Center)
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
            Headline6(text = "New highscore", modifier = Gravity.Center)
        }
        HeightSpacer(16.dp)
        TextImageButton(
            text = "Play random game",
            drawableResource = R.drawable.ic_icons8_dice,
            modifier = Gravity.Center,
            onClick = { random() })
        HeightSpacer(8.dp)
        TextImageButton(
            text = "Play again",
            drawableResource = R.drawable.ic_icons8_recurring_appointment,
            modifier = Gravity.Center,
            onClick = { again() })
        HeightSpacer(8.dp)
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            modifier = Gravity.Center,
            onClick = { gameMaster.start() })
    }
}
