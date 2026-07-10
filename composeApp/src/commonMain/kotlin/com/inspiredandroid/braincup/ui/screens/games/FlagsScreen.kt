package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.screens.countryNameRes
import com.inspiredandroid.braincup.ui.screens.flagResource
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun ColumnScope.FlagsContent(
    uiState: FlagsUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val flagSize = if (compact) 120.dp else 180.dp

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(flagResource(uiState.countrySlug)),
                    contentDescription = null,
                    modifier = Modifier.size(flagSize),
                )
                Spacer(Modifier.height(8.dp))
                FlagsScoreRow(uiState.currentScore, uiState.bestScore)
            }
            FlagAnswerButtons(
                uiState = uiState,
                onAnswer = onAnswer,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = 280.dp),
            )
        }
    } else {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val numAnswers = uiState.possibleAnswers.size
            val buttonsApprox = (52.dp * numAnswers) +
                (8.dp * (numAnswers - 1).coerceAtLeast(0))
            val headerApprox = 72.dp
            val minBreathingRoom = 16.dp
            val dynamicFlagSize = (maxHeight - buttonsApprox - headerApprox - minBreathingRoom)
                .coerceIn(80.dp, flagSize)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlagsScoreRow(
                    currentScore = uiState.currentScore,
                    bestScore = uiState.bestScore,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Spacer(Modifier.weight(1f))
                Image(
                    painter = painterResource(flagResource(uiState.countrySlug)),
                    contentDescription = null,
                    modifier = Modifier.size(dynamicFlagSize),
                )
                Spacer(Modifier.weight(1f))
                FlagAnswerButtons(
                    uiState = uiState,
                    onAnswer = onAnswer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .widthIn(max = 480.dp),
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FlagsScoreRow(
    currentScore: Int,
    bestScore: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.game_flags_score, currentScore),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (bestScore > 0) {
            Text(
                text = stringResource(Res.string.game_flags_best, bestScore),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FlagAnswerButtons(
    uiState: FlagsUiState,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        uiState.possibleAnswers.forEach { button ->
            FlagAnswerButton(button = button, onAnswer = onAnswer)
        }
    }
}

@Composable
private fun FlagAnswerButton(
    button: AnswerButton,
    onAnswer: (String) -> Unit,
) {
    val label = stringResource(countryNameRes(button.value))
    val face = when (button.state) {
        AnswerButtonState.NORMAL, AnswerButtonState.DIMMED -> Primary
        AnswerButtonState.WRONG -> MaterialTheme.colorScheme.errorContainer
        AnswerButtonState.CORRECT -> SuccessGreen
    }
    val textColor = when (button.state) {
        AnswerButtonState.WRONG -> MaterialTheme.colorScheme.onErrorContainer
        else -> ComposeColor.White
    }
    val isClickable = button.state == AnswerButtonState.NORMAL
    val containerModifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 52.dp)
        .hoverHand(isClickable)
        .alpha(if (button.state == AnswerButtonState.DIMMED) 0.4f else 1f)

    PrismTile(
        face = face,
        modifier = containerModifier,
        isClickable = isClickable,
        onClick = { onAnswer(button.value) },
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@GameDevicePreviews
@Composable
private fun FlagsContentPreview() {
    GamePreviewHost {
        FlagsContent(
            uiState = FlagsUiState(
                countrySlug = "germany",
                possibleAnswers = persistentListOf(
                    AnswerButton("Germany"),
                    AnswerButton("France"),
                    AnswerButton("Italy"),
                    AnswerButton("Spain"),
                ),
                currentScore = 3,
                bestScore = 12,
            ),
            onAnswer = {},
        )
    }
}
