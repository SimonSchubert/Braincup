package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_start
import braincup.composeapp.generated.resources.instructions_best_score
import braincup.composeapp.generated.resources.instructions_leaderboard
import braincup.composeapp.generated.resources.mini_chess_difficulty
import braincup.composeapp.generated.resources.mini_chess_difficulty_easy
import braincup.composeapp.generated.resources.mini_chess_difficulty_hard
import braincup.composeapp.generated.resources.mini_chess_difficulty_medium
import braincup.composeapp.generated.resources.wordle_legend_absent
import braincup.composeapp.generated.resources.wordle_legend_correct
import braincup.composeapp.generated.resources.wordle_legend_present
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.formattedScore
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChessMoveDemo
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordleCorrect
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InstructionsScreen(
    gameType: GameType,
    storage: UserStorage,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onShowLeaderboard: (() -> Unit)? = null,
) {
    AppScaffold(
        title = stringResource(gameType.displayNameRes),
        onBack = onBack,
        // The animated demos are taller than a text blurb, so let the screen scroll for them.
        scrollable = hasAnimatedInstructions(gameType),
    ) {
        Spacer(Modifier.height(32.dp))

        if (hasAnimatedInstructions(gameType)) {
            ChessMoveDemo(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
            )
        } else {
            Text(
                text = stringResource(gameType.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }

        if (gameType == GameType.WORDLE) {
            Spacer(Modifier.height(24.dp))
            WordleColorLegend(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }

        if (gameType == GameType.MINI_CHESS) {
            Spacer(Modifier.height(32.dp))
            MiniChessDifficultySelector(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally),
                initial = storage.getMiniChessDifficulty(),
                onSelected = { storage.setMiniChessDifficulty(it) },
            )
        }

        if (gameType.hasLeaderboard) {
            val highscore = storage.getHighScore(gameType.id)
            if (highscore > 0) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(
                        Res.string.instructions_best_score,
                        gameType.formattedScore(highscore),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onStart,
            value = stringResource(Res.string.button_start),
        )

        if (gameType.hasLeaderboard && onShowLeaderboard != null) {
            Spacer(Modifier.height(16.dp))
            TextPrismButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onShowLeaderboard,
                value = stringResource(Res.string.instructions_leaderboard),
            )
        }
    }
}

// Games listed here replace their text description with an animated demo on the instructions
// screen. Add a game's branch (and its demo composable) here to opt it in.
private fun hasAnimatedInstructions(gameType: GameType): Boolean = gameType == GameType.MINI_CHESS

@Composable
private fun WordleColorLegend(modifier: Modifier = Modifier) {
    // One swatch per line, matching the board tiles, so the green/yellow/gray meaning is shown
    // visually instead of spelled out in the description.
    val entries = listOf(
        Triple(WordleCorrect, 'A', Res.string.wordle_legend_correct),
        Triple(WordlePresent, 'B', Res.string.wordle_legend_present),
        Triple(WordleAbsent, 'C', Res.string.wordle_legend_absent),
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        entries.forEach { (face, letter, labelRes) ->
            WordleLegendRow(face = face, letter = letter, labelRes = labelRes)
        }
    }
}

@Composable
private fun WordleLegendRow(face: Color, letter: Char, labelRes: StringResource) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PrismCard(
            face = face,
            modifier = Modifier.size(44.dp),
        ) {
            Text(
                text = letter.toString(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun MiniChessDifficultySelector(
    initial: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val supported = listOf(1, 3, 5)
    val resolvedInitial = if (initial in supported) initial else 3
    var selected by remember { mutableIntStateOf(resolvedInitial) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.mini_chess_difficulty),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        // Spread depth widely so users feel a real strength jump:
        //   Easy=1 (no opponent-response prediction → easy to trap)
        //   Medium=3 (sees player + own follow-up)
        //   Hard=5 (deep tactical calculation; slower thinks)
        val options = listOf(
            1 to stringResource(Res.string.mini_chess_difficulty_easy),
            3 to stringResource(Res.string.mini_chess_difficulty_medium),
            5 to stringResource(Res.string.mini_chess_difficulty_hard),
        )
        Row(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            options.forEach { (depth, label) ->
                val isSelected = selected == depth
                PrismTile(
                    face = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .hoverHand()
                        .defaultMinSize(minHeight = 48.dp),
                    onClick = {
                        selected = depth
                        onSelected(depth)
                    },
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}
