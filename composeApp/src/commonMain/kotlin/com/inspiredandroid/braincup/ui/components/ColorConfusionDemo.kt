package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_done
import braincup.composeapp.generated.resources.game_color_confusion_desc
import braincup.composeapp.generated.resources.color_confusion_demo_title
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceDark
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceLight
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.UnselectedTileFaceDark
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import com.inspiredandroid.braincup.games.tools.Color as GameColor

// A cell shows a colour-word printed in some ink colour. It is a "match" when the word's meaning
// equals its ink (e.g. RED printed in red). Three of the nine cells match; the demo taps those.
private data class DemoWord(val word: GameColor, val ink: GameColor) {
    val matches: Boolean get() = word == ink
}

private val Cells = listOf(
    DemoWord(GameColor.RED, GameColor.RED), // match
    DemoWord(GameColor.BLUE, GameColor.GREEN),
    DemoWord(GameColor.GREEN, GameColor.RED),
    DemoWord(GameColor.YELLOW, GameColor.YELLOW), // match
    DemoWord(GameColor.PURPLE, GameColor.BLUE),
    DemoWord(GameColor.RED, GameColor.GREEN),
    DemoWord(GameColor.GREEN, GameColor.GREEN), // match
    DemoWord(GameColor.BLUE, GameColor.PURPLE),
    DemoWord(GameColor.ORANGE, GameColor.RED),
)
private val MatchIndices = Cells.withIndex().filter { it.value.matches }.map { it.index }

private const val SelectStaggerMillis = 700L
private const val BeforeSubmitMillis = 500L
private const val SolvedHoldMillis = 1700L
private const val ResetPauseMillis = 500L
private const val TransitionMillis = 250

/**
 * Animated tutorial for Color Confusion: a 3x3 Stroop grid where the demo taps every cell whose word
 * meaning equals its ink colour, then presses Done so those cells turn green. Mirrors
 * ColorConfusionGame (select the matching cells, then submit). Loops on its own, like [LightsOutDemo].
 */
@Composable
fun ColorConfusionDemo(modifier: Modifier = Modifier) {
    var selected by remember { mutableStateOf(emptySet<Int>()) }
    var submitted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            selected = emptySet()
            submitted = false
            delay(ResetPauseMillis)

            for (index in MatchIndices) {
                selected = selected + index
                delay(SelectStaggerMillis)
            }
            delay(BeforeSubmitMillis)
            submitted = true
            delay(SolvedHoldMillis)
        }
    }

    val cell = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.color_confusion_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0 until 3) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        DemoConfusionCell(
                            cell = Cells[index],
                            isSelected = index in selected,
                            isCorrect = submitted && Cells[index].matches,
                            size = cell,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        PrismTile(
            face = Primary,
            isClickable = false,
            isSelected = submitted,
            onClick = {},
            modifier = Modifier.defaultMinSize(minWidth = 96.dp, minHeight = 48.dp),
        ) {
            Text(stringResource(Res.string.button_done), color = Color.White)
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_color_confusion_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoConfusionCell(cell: DemoWord, isSelected: Boolean, isCorrect: Boolean, size: Dp) {
    val isDark = isSystemInDarkTheme()
    val selectedFace = if (isDark) SelectedTileFaceDark else SelectedTileFaceLight
    val unselectedFace = if (isDark) UnselectedTileFaceDark else MaterialTheme.colorScheme.surfaceContainer
    val target = when {
        isCorrect -> SuccessGreenSoft
        isSelected -> selectedFace
        else -> unselectedFace
    }
    val face by animateColorAsState(target, tween(TransitionMillis), label = "confusionCell")
    PrismTile(
        face = face,
        modifier = Modifier.size(size),
        isClickable = false,
        isSelected = isCorrect,
        onClick = {},
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = cell.word.localizedName(),
                style = MaterialTheme.typography.titleSmall,
                color = cell.ink.composeColor(),
            )
        }
    }
}
