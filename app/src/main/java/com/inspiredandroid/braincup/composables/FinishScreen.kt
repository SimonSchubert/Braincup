package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
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
        Headline4(text = "Score: $rank", modifier = LayoutGravity.Center)
        Spacer(LayoutHeight(16.dp))
        if (answeredAllCorrect) {
            Text(
                "You got 1 extra point for making zero mistakes.",
                style = MaterialTheme.typography().h6.merge(ParagraphStyle(textAlign = TextAlign.Center)),
                modifier = LayoutGravity.Center
            )
        }
        if (newHighscore) {
            Spacer(LayoutHeight(8.dp))
            Headline6(text = "New highscore", modifier = LayoutGravity.Center)
        }
        Spacer(LayoutHeight(16.dp))
        TextImageButton(
            text = "Play random game",
            drawableResource = R.drawable.ic_icons8_dice,
            modifier = LayoutGravity.Center,
            onClick = { random() })
        Spacer(LayoutHeight(16.dp))
        TextImageButton(
            text = "Play again",
            drawableResource = R.drawable.ic_icons8_recurring_appointment,
            modifier = LayoutGravity.Center,
            onClick = { again() })
        Spacer(LayoutHeight(16.dp))
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            modifier = LayoutGravity.Center,
            onClick = { gameMaster.start() })
    }
}
