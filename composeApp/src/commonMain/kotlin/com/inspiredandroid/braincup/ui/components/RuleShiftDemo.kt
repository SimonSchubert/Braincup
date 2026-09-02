package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.rule_shift_demo_caption_match
import braincup.composeapp.generated.resources.rule_shift_demo_caption_new_rule
import braincup.composeapp.generated.resources.rule_shift_demo_caption_rule_moved
import braincup.composeapp.generated.resources.rule_shift_demo_caption_rule_works
import braincup.composeapp.generated.resources.rule_shift_demo_title
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.RuleShiftCard
import com.inspiredandroid.braincup.games.RuleShiftGame
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay

// Three cards, each pointing at a different key card under each of the three rules, so every step
// below is unambiguous about which rule was being used.
private val FirstCard = RuleShiftCard(2, GameColor.BLUE, Shape.CROSS)
private val SecondCard = RuleShiftCard(3, GameColor.RED, Shape.CIRCLE)
private val ThirdCard = RuleShiftCard(1, GameColor.GREEN, Shape.CROSS)

private const val BlueKey = 4
private const val RedKey = 1
private const val GreenKey = 2
private const val CountOneKey = 1

private const val ReadCardMillis = 1500L
private const val RevealHoldMillis = 1400L
private const val ShiftBeatMillis = 2400L

private val Captions = persistentListOf(
    Res.string.rule_shift_demo_caption_match,
    Res.string.rule_shift_demo_caption_rule_works,
    Res.string.rule_shift_demo_caption_rule_moved,
    Res.string.rule_shift_demo_caption_new_rule,
)

/**
 * Animated tutorial for Rule Shift, built around the one beat that carries the game: a sort that
 * worked twice comes back wrong, and nothing on screen says why.
 *
 * The card slides to sit under the key card it is sorted onto rather than being marked in place.
 * Without that movement the tutorial shows only *that* an answer was accepted, and the player is
 * left to notice the shared colour unaided; the card travelling to the key it matches is what makes
 * the pairing readable at a glance.
 *
 * The first two cards are sorted by colour and confirmed. The third is sorted by colour the same
 * way and rejected, which is the only signal the player ever gets that the rule moved; it is then
 * re-sorted by count and accepted. A tutorial that only showed correct sorts would teach the
 * mechanic and hide the task.
 */
@Composable
fun RuleShiftDemo(modifier: Modifier = Modifier) {
    var card by remember { mutableStateOf(FirstCard) }
    var tappedKey by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf(true) }
    var caption by remember { mutableStateOf(Captions[0]) }

    LaunchedEffect(Unit) {
        while (true) {
            caption = Res.string.rule_shift_demo_caption_match
            card = FirstCard
            tappedKey = null
            delay(ReadCardMillis)
            tappedKey = BlueKey
            isCorrect = true
            caption = Res.string.rule_shift_demo_caption_rule_works
            delay(RevealHoldMillis)

            // A second confirmation, so colour feels settled before it stops working.
            card = SecondCard
            tappedKey = null
            delay(ReadCardMillis)
            tappedKey = RedKey
            isCorrect = true
            delay(RevealHoldMillis)

            // Same reasoning, and now it is wrong. This is the whole game.
            card = ThirdCard
            tappedKey = null
            delay(ReadCardMillis)
            tappedKey = GreenKey
            isCorrect = false
            caption = Res.string.rule_shift_demo_caption_rule_moved
            delay(ShiftBeatMillis)

            // The recovery: the same card, sorted on a different dimension.
            tappedKey = null
            delay(ReadCardMillis)
            tappedKey = CountOneKey
            isCorrect = true
            caption = Res.string.rule_shift_demo_caption_new_rule
            delay(ShiftBeatMillis)
        }
    }

    val compact = LocalIsCompactHeight.current
    val keySymbol = if (compact) 11.dp else 14.dp
    val cardSymbol = if (compact) 17.dp else 22.dp
    val tileSize = if (compact) 52.dp else 64.dp

    // Centre the card under the key it was sorted onto. Slots are 1-based across a row of four, so
    // slot 1 sits one and a half tiles left of centre and slot 4 the same distance right.
    val slide by animateDpAsState(
        targetValue = tappedKey?.let { tileSize * (it - 2.5f) } ?: 0.dp,
        animationSpec = tween(durationMillis = 320),
    )

    DemoScaffold(title = Res.string.rule_shift_demo_title, modifier = modifier) {
        Row {
            RuleShiftGame.keyCards.forEachIndexed { index, keyCard ->
                val slot = index + 1
                val state = when {
                    tappedKey != slot -> AnswerFeedbackState.NORMAL
                    isCorrect -> AnswerFeedbackState.CORRECT
                    else -> AnswerFeedbackState.WRONG
                }
                DemoKeyTile(
                    card = keyCard,
                    state = state,
                    symbolSize = keySymbol,
                    modifier = Modifier.size(tileSize).aspectRatio(1f).padding(4.dp),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        RuleShiftCardFace(card = card, symbolSize = cardSymbol, modifier = Modifier.offset(x = slide))

        Spacer(Modifier.height(16.dp))

        DemoCaption(
            current = caption,
            all = Captions,
            emphasis = persistentSetOf(Res.string.rule_shift_demo_caption_rule_moved),
        )
    }
}

@Composable
private fun DemoKeyTile(
    card: RuleShiftCard,
    state: AnswerFeedbackState,
    symbolSize: Dp,
    modifier: Modifier = Modifier,
) {
    PrismTile(face = ruleShiftKeyFace(state), isClickable = false, onClick = {}, modifier = modifier) {
        RuleShiftCardFace(card = card, symbolSize = symbolSize)
    }
}
