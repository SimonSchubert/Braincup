package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.localizedName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnswerFeedbackScreen(
    isCorrect: Boolean,
    message: FeedbackMessage?,
) {
    GameScaffold {
        if (isCorrect) {
            Image(
                painterResource(Res.drawable.ic_success),
                contentDescription = null,
                modifier = Modifier
                    .size(360.dp),
            )
        } else {
            Image(
                painterResource(Res.drawable.ic_delivery),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp),
            )
            if (message != null) {
                Text(
                    text = stringResource(Res.string.feedback_solution, message.toLocalizedString()),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedbackMessage.toLocalizedString(): String = when (this) {
    is FeedbackMessage.Plain -> text
    is FeedbackMessage.FigureDescription -> {
        val colorName = color.localizedName()
        val shapeName = shape.localizedName()
        if (directionDegrees != null) {
            val dir = when (directionDegrees) {
                0 -> stringResource(Res.string.direction_up)
                90 -> stringResource(Res.string.direction_right)
                180 -> stringResource(Res.string.direction_down)
                270 -> stringResource(Res.string.direction_left)
                else -> ""
            }
            stringResource(Res.string.solution_pointing, colorName, shapeName, dir)
        } else {
            stringResource(Res.string.solution_figure, colorName, shapeName)
        }
    }
    is FeedbackMessage.GridPosition -> stringResource(Res.string.solution_column_row, column, row)
    is FeedbackMessage.SideCount -> {
        val side = if (isLeft) stringResource(Res.string.solution_left) else stringResource(Res.string.solution_right)
        "$side ($count)"
    }
}
