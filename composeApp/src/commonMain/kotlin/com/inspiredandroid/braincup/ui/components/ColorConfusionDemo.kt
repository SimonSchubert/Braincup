package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.color_confusion_demo_caption_agree
import braincup.composeapp.generated.resources.color_confusion_demo_caption_ink
import braincup.composeapp.generated.resources.color_confusion_demo_caption_recover
import braincup.composeapp.generated.resources.color_confusion_demo_caption_trap
import braincup.composeapp.generated.resources.color_confusion_demo_title
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.localizedName
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay

// Slots are 1-based, matching the swatch positions the game hands the controller.
private const val RedSlot = 1
private const val GreenSlot = 2
private const val BlueSlot = 3
private const val YellowSlot = 4

private const val ReadWordMillis = 1300L
private const val RevealHoldMillis = 1300L
private const val TrapBeatMillis = 1900L

private val Captions = persistentListOf(
    Res.string.color_confusion_demo_caption_ink,
    Res.string.color_confusion_demo_caption_agree,
    Res.string.color_confusion_demo_caption_trap,
    Res.string.color_confusion_demo_caption_recover,
)

/**
 * Animated tutorial for Color Confusion, built around the beat that carries the game: the demo
 * reads the word instead of the ink and gets it wrong.
 *
 * A tutorial made only of correct answers would teach the mechanic - tap a swatch - and hide the
 * task, which is that the word supplies a wrong answer faster than the ink supplies the right one.
 * So the third word is answered the way a player's reflex answers it, marked wrong, and then
 * answered again correctly.
 *
 * The second word is deliberately congruent. Without one the player would learn "the answer is
 * never the word", which is a shortcut the real list does not allow.
 */
@Composable
fun ColorConfusionDemo(modifier: Modifier = Modifier) {
    var word by remember { mutableStateOf(GameColor.BLUE) }
    var ink by remember { mutableStateOf(GameColor.RED) }
    var tappedSlot by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf(true) }
    var caption by remember { mutableStateOf(Captions[0]) }

    LaunchedEffect(Unit) {
        while (true) {
            // Incongruent, answered correctly: the rule, stated by example.
            word = GameColor.BLUE
            ink = GameColor.RED
            tappedSlot = null
            caption = Res.string.color_confusion_demo_caption_ink
            delay(ReadWordMillis)
            tappedSlot = RedSlot
            isCorrect = true
            delay(RevealHoldMillis)

            // Congruent, so the rule cannot be misread as "never the word".
            word = GameColor.GREEN
            ink = GameColor.GREEN
            tappedSlot = null
            caption = Res.string.color_confusion_demo_caption_agree
            delay(ReadWordMillis)
            tappedSlot = GreenSlot
            isCorrect = true
            delay(RevealHoldMillis)

            // The trap: the word is answered instead of the ink, and it is wrong.
            word = GameColor.YELLOW
            ink = GameColor.BLUE
            tappedSlot = null
            caption = Res.string.color_confusion_demo_caption_ink
            delay(ReadWordMillis)
            tappedSlot = YellowSlot
            isCorrect = false
            caption = Res.string.color_confusion_demo_caption_trap
            delay(TrapBeatMillis)

            // The recovery: same word, answered on its ink.
            tappedSlot = null
            delay(ReadWordMillis)
            tappedSlot = BlueSlot
            isCorrect = true
            caption = Res.string.color_confusion_demo_caption_recover
            delay(TrapBeatMillis)
        }
    }

    val compact = LocalIsCompactHeight.current
    val tileSize = if (compact) 52.dp else 64.dp
    val markSize = if (compact) 18.dp else 22.dp

    DemoScaffold(title = Res.string.color_confusion_demo_title, modifier = modifier) {
        Text(
            text = word.localizedName().uppercase(),
            style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = ink.composeColor(),
        )

        Spacer(Modifier.height(if (compact) 16.dp else 24.dp))

        Row {
            ColorConfusionGame.RESPONSE_COLORS.forEachIndexed { index, color ->
                val slot = index + 1
                val state = when {
                    tappedSlot != slot -> AnswerFeedbackState.NORMAL
                    isCorrect -> AnswerFeedbackState.CORRECT
                    else -> AnswerFeedbackState.WRONG
                }
                DemoSwatchTile(
                    color = color,
                    state = state,
                    markSize = markSize,
                    modifier = Modifier.size(tileSize).aspectRatio(1f).padding(4.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        DemoCaption(
            current = caption,
            all = Captions,
            emphasis = persistentSetOf(Res.string.color_confusion_demo_caption_trap),
        )
    }
}

@Composable
private fun DemoSwatchTile(
    color: GameColor,
    state: AnswerFeedbackState,
    markSize: Dp,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = color.composeColor(),
        isClickable = false,
        isSelected = state != AnswerFeedbackState.NORMAL,
        onClick = {},
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            when (state) {
                AnswerFeedbackState.CORRECT -> ChunkyCheck(Color.White, Modifier.size(markSize))
                AnswerFeedbackState.WRONG -> ChunkyCross(Color.White, Modifier.size(markSize))
                else -> Unit
            }
        }
    }
}
