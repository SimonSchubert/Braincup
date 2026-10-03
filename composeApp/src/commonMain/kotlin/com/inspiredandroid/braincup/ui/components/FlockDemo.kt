package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.flock_demo_caption_agree
import braincup.composeapp.generated.resources.flock_demo_caption_middle
import braincup.composeapp.generated.resources.flock_demo_caption_recover
import braincup.composeapp.generated.resources.flock_demo_caption_trap
import braincup.composeapp.generated.resources.flock_demo_title
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.FlockGame.Direction
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay

private const val LookMillis = 1300L
private const val SwipeMillis = 350
private const val RevealHoldMillis = 1300L
private const val TrapBeatMillis = 1900L

private val Captions = persistentListOf(
    Res.string.flock_demo_caption_middle,
    Res.string.flock_demo_caption_agree,
    Res.string.flock_demo_caption_trap,
    Res.string.flock_demo_caption_recover,
)

/**
 * Animated tutorial for Flock, built the way Color Confusion's is: the third flock is answered the
 * way the flankers pull, marked wrong, then answered on the middle bird. A congruent flock sits in
 * the middle of the loop so the rule cannot be misread as "never the way the flock flies".
 */
@Composable
fun FlockDemo(modifier: Modifier = Modifier) {
    var target by remember { mutableStateOf(Direction.LEFT) }
    var flankers by remember { mutableStateOf(Direction.RIGHT) }
    var swipe by remember { mutableStateOf<Direction?>(null) }
    var feedback by remember { mutableStateOf(AnswerFeedbackState.NORMAL) }
    var caption by remember { mutableStateOf(Captions[0]) }
    val swipeProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        suspend fun show(newTarget: Direction, newFlankers: Direction) {
            target = newTarget
            flankers = newFlankers
            swipe = null
            feedback = AnswerFeedbackState.NORMAL
            delay(LookMillis)
        }

        suspend fun answer(direction: Direction) {
            swipe = direction
            swipeProgress.snapTo(0f)
            swipeProgress.animateTo(1f, tween(SwipeMillis))
            feedback = if (direction == target) AnswerFeedbackState.CORRECT else AnswerFeedbackState.WRONG
        }

        while (true) {
            caption = Res.string.flock_demo_caption_middle
            show(Direction.LEFT, Direction.RIGHT)
            answer(Direction.LEFT)
            delay(RevealHoldMillis)

            caption = Res.string.flock_demo_caption_agree
            show(Direction.UP, Direction.UP)
            answer(Direction.UP)
            delay(RevealHoldMillis)

            caption = Res.string.flock_demo_caption_middle
            show(Direction.DOWN, Direction.UP)
            answer(Direction.UP)
            caption = Res.string.flock_demo_caption_trap
            delay(TrapBeatMillis)

            swipe = null
            feedback = AnswerFeedbackState.NORMAL
            delay(LookMillis)
            answer(Direction.DOWN)
            caption = Res.string.flock_demo_caption_recover
            delay(TrapBeatMillis)
        }
    }

    val compact = LocalIsCompactHeight.current
    val trailColor = MaterialTheme.colorScheme.onSurfaceVariant

    DemoScaffold(title = Res.string.flock_demo_title, modifier = modifier) {
        FlockRow(
            target = target,
            flankers = flankers,
            feedback = feedback,
            modifier = Modifier.width(if (compact) 220.dp else 260.dp),
        )

        Spacer(Modifier.height(12.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(if (compact) 48.dp else 64.dp),
        ) {
            when (feedback) {
                AnswerFeedbackState.CORRECT -> ChunkyCheck(SuccessGreen, Modifier.size(28.dp))
                AnswerFeedbackState.WRONG -> ChunkyCross(ErrorRed, Modifier.size(28.dp))
                else -> {
                    val direction = swipe
                    if (direction != null) {
                        Canvas(Modifier.fillMaxSize()) {
                            val reach = size.minDimension / 2f * swipeProgress.value
                            val unit = direction.unitOffset()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val tip = center + unit * reach
                            drawLine(
                                color = trailColor.copy(alpha = 0.4f),
                                start = center,
                                end = tip,
                                strokeWidth = 6.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                            drawCircle(trailColor, radius = 7.dp.toPx(), center = tip)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        DemoCaption(
            current = caption,
            all = Captions,
            emphasis = persistentSetOf(Res.string.flock_demo_caption_trap),
        )
    }
}

private fun Direction.unitOffset(): Offset = when (this) {
    Direction.UP -> Offset(0f, -1f)
    Direction.RIGHT -> Offset(1f, 0f)
    Direction.DOWN -> Offset(0f, 1f)
    Direction.LEFT -> Offset(-1f, 0f)
}
