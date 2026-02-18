package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Shape

sealed interface FeedbackMessage {
    data class Plain(val text: String) : FeedbackMessage
    data class FigureDescription(
        val color: Color,
        val shape: Shape,
        val directionDegrees: Int?,
    ) : FeedbackMessage
    data class GridPosition(val column: Int, val row: Int) : FeedbackMessage
    data class SideCount(val isLeft: Boolean, val count: Int) : FeedbackMessage
}
