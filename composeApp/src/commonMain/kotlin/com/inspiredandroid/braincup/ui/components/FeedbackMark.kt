package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.ui.theme.SuccessGreen

enum class FeedbackMarkKind { CORRECT, WRONG }

fun AnswerFeedbackState.markKind(): FeedbackMarkKind? = when (this) {
    AnswerFeedbackState.CORRECT -> FeedbackMarkKind.CORRECT
    AnswerFeedbackState.WRONG -> FeedbackMarkKind.WRONG
    else -> null
}

/**
 * A tick or a cross on a surface-coloured disc.
 *
 * Right and wrong are told apart by the glyph, never by the tint alone: red against green is the
 * one pair that a red-green colour-blind player cannot read, and a reveal that lasts under a
 * second leaves no time to work it out from context. The disc gives the glyph the same contrast
 * whatever the tile underneath is painted.
 */
@Composable
fun FeedbackMark(kind: FeedbackMarkKind, modifier: Modifier = Modifier) {
    val ink = when (kind) {
        FeedbackMarkKind.CORRECT -> SuccessGreen
        FeedbackMarkKind.WRONG -> MaterialTheme.colorScheme.error
    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, ink, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val glyph = Modifier.fillMaxSize(0.5f)
        when (kind) {
            FeedbackMarkKind.CORRECT -> ChunkyCheck(ink, glyph)
            FeedbackMarkKind.WRONG -> ChunkyCross(ink, glyph)
        }
    }
}

@Composable
fun BoxScope.FeedbackCornerMark(
    kind: FeedbackMarkKind?,
    size: Dp = FeedbackCornerMarkSize,
    alignment: Alignment = Alignment.TopEnd,
) {
    if (kind == null) return
    FeedbackMark(kind, Modifier.align(alignment).padding(3.dp).size(size))
}

@Composable
fun BoxScope.FeedbackCornerMark(
    state: AnswerFeedbackState,
    size: Dp = FeedbackCornerMarkSize,
    alignment: Alignment = Alignment.TopEnd,
) = FeedbackCornerMark(state.markKind(), size, alignment)

val FeedbackCornerMarkSize = 22.dp

/** [FeedbackMark] for boards drawn on a canvas, which cannot host a composable per cell. */
fun DrawScope.drawFeedbackMark(
    kind: FeedbackMarkKind,
    center: Offset,
    radius: Float,
    colors: FeedbackMarkColors,
) {
    val ink = when (kind) {
        FeedbackMarkKind.CORRECT -> colors.correct
        FeedbackMarkKind.WRONG -> colors.wrong
    }
    drawCircle(colors.disc, radius, center)
    drawCircle(ink, radius, center, style = Stroke(width = radius * 0.14f))
    val glyph = Size(radius, radius)
    val topLeft = center - Offset(radius / 2, radius / 2)
    when (kind) {
        FeedbackMarkKind.CORRECT -> drawChunkyCheck(ink, topLeft, glyph)
        FeedbackMarkKind.WRONG -> drawChunkyCross(ink, topLeft, glyph)
    }
}

data class FeedbackMarkColors(val disc: Color, val correct: Color, val wrong: Color)

@Composable
fun feedbackMarkColors(): FeedbackMarkColors = FeedbackMarkColors(
    disc = MaterialTheme.colorScheme.surface,
    correct = SuccessGreen,
    wrong = MaterialTheme.colorScheme.error,
)

/** A corner mark big enough to read on a large tile without burying the content of a small one. */
fun feedbackMarkSizeFor(tile: Dp): Dp = (tile * 0.3f).coerceIn(14.dp, 26.dp)
