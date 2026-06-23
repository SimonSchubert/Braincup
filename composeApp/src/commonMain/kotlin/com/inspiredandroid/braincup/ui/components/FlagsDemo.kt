package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.flags_demo_title
import braincup.composeapp.generated.resources.game_flags_desc
import com.inspiredandroid.braincup.ui.screens.countryNameRes
import com.inspiredandroid.braincup.ui.screens.flagResource
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class DemoFlagRound(
    val correct: String,
    val options: List<String>,
)

// Two recognisable rounds with two options each (kept to two to stay compact on the instructions
// screen); the correct option's position alternates so it is not always first.
private val Rounds = listOf(
    DemoFlagRound(correct = "japan", options = listOf("japan", "france")),
    DemoFlagRound(correct = "france", options = listOf("brazil", "france")),
)

private const val ShowMillis = 1100L
private const val RevealMillis = 1400L
private const val ResetPauseMillis = 500L

/**
 * Animated tutorial for Flags: a flag is shown and, after a beat, the correct country name lights up
 * green among the answer options. Cycles through a couple of flags. Mirrors FlagsGame's flag →
 * multiple-choice round. Loops on its own, like [LightsOutDemo].
 */
@Composable
fun FlagsDemo(modifier: Modifier = Modifier) {
    var roundIndex by remember { mutableStateOf(0) }
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            revealed = false
            delay(ResetPauseMillis)
            delay(ShowMillis)
            revealed = true
            delay(RevealMillis)
            roundIndex = (roundIndex + 1) % Rounds.size
        }
    }

    val round = Rounds[roundIndex]
    val flagSize = if (LocalIsCompactHeight.current) 110.dp else 150.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.flags_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Image(
            painter = painterResource(flagResource(round.correct)),
            contentDescription = null,
            modifier = Modifier.size(flagSize),
        )
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            round.options.forEach { slug ->
                val isCorrect = revealed && slug == round.correct
                PrismTile(
                    face = if (isCorrect) SuccessGreen else Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    isClickable = false,
                    onClick = {},
                ) {
                    Text(
                        text = stringResource(countryNameRes(slug)),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_flags_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
