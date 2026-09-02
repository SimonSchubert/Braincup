package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.RuleShiftCard
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft

/**
 * A Rule Shift card, drawn the same way on the board and in the tutorial.
 *
 * The two were written twice and have to stay identical: the demo teaches the player to sort by
 * colour, shape or count, and a card that reads differently in the tutorial than in the game
 * teaches the wrong thing.
 *
 * Symbols sit in rows of at most two, so a count of three reads as two-then-one and four as a
 * square. Every symbol is the same size whatever the count, so a card cannot be sorted by how much
 * ink is on it instead of by how many symbols there are.
 */
@Composable
fun RuleShiftCardFace(
    card: RuleShiftCard,
    symbolSize: Dp,
    modifier: Modifier = Modifier,
) {
    val figure = Figure(card.shape, card.color)
    val rows = when (card.count) {
        1 -> listOf(1)
        2 -> listOf(2)
        3 -> listOf(2, 1)
        else -> listOf(2, 2)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        rows.forEach { symbolsInRow ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(symbolsInRow) {
                    ShapeCanvas(figure = figure, modifier = Modifier.size(symbolSize))
                }
            }
        }
    }
}

/** The face colour of a key tile a card can be sorted onto, marked once the sort is judged. */
@Composable
fun ruleShiftKeyFace(state: AnswerFeedbackState): Color = when (state) {
    AnswerFeedbackState.WRONG -> MaterialTheme.colorScheme.errorContainer
    AnswerFeedbackState.CORRECT -> SuccessGreenSoft
    else -> MaterialTheme.colorScheme.surfaceContainer
}
