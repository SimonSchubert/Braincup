package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.FlockGame
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.FlockBirdBlue
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf

// A swept-wing bird seen from above, nose to the right. It doubles as an arrowhead on purpose:
// the flanker task is defined over arrows, and the bird must read as one at a glance.
private val BirdPointingRight = persistentListOf(
    0.96f to 0.50f,
    0.08f to 0.10f,
    0.36f to 0.50f,
    0.08f to 0.90f,
)

@Composable
fun FlockBird(
    direction: FlockGame.Direction,
    face: Color,
    modifier: Modifier = Modifier,
) {
    PrismPolygon(
        points = BirdPointingRight,
        face = face,
        rotationDegrees = direction.rotationDegrees,
        modifier = modifier,
    )
}

/**
 * Five birds in one row. Only the middle one is ever recoloured, and only after the answer: until
 * then the flock is one hue, so the target can be found by its place in the row and nothing else.
 */
@Composable
fun FlockRow(
    target: FlockGame.Direction,
    flankers: FlockGame.Direction,
    feedback: AnswerFeedbackState,
    modifier: Modifier = Modifier,
) {
    val targetFace = when (feedback) {
        AnswerFeedbackState.CORRECT -> SuccessGreen
        AnswerFeedbackState.WRONG -> ErrorRed
        else -> FlockBirdBlue
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier,
    ) {
        val birdCount = FlockGame.FLANKERS_PER_SIDE * 2 + 1
        repeat(birdCount) { index ->
            val isTarget = index == FlockGame.FLANKERS_PER_SIDE
            FlockBird(
                direction = if (isTarget) target else flankers,
                face = if (isTarget) targetFace else FlockBirdBlue,
                modifier = Modifier.weight(1f).aspectRatio(1f),
            )
        }
    }
}
