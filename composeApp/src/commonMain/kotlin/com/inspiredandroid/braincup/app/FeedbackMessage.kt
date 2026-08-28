package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape

@Immutable
sealed interface FeedbackMessage {
    data class Plain(val text: String) : FeedbackMessage
    data class FigureDescription(
        val color: GameColor,
        val shape: Shape,
        val directionDegrees: Int?,
    ) : FeedbackMessage
    data class GridPosition(val column: Int, val row: Int) : FeedbackMessage
    data class SideCount(val isLeft: Boolean, val count: Int) : FeedbackMessage

    /** "Remember 7", the running total Mental Calculation asks the player to hold on to. */
    data class Remember(val number: Int) : FeedbackMessage
}
