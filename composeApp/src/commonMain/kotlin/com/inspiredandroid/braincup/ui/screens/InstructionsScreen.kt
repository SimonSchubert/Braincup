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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_start
import braincup.composeapp.generated.resources.mini_chess_difficulty
import braincup.composeapp.generated.resources.mini_chess_difficulty_easy
import braincup.composeapp.generated.resources.mini_chess_difficulty_hard
import braincup.composeapp.generated.resources.mini_chess_difficulty_medium
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.hoverHand
import org.jetbrains.compose.resources.stringResource

@Composable
fun InstructionsScreen(
    gameType: GameType,
    storage: UserStorage,
    onStart: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(gameType.displayNameRes),
        onBack = onBack,
        scrollable = false,
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(gameType.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally),
        )

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

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onStart,
            value = stringResource(Res.string.button_start),
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
        SingleChoiceSegmentedButtonRow {
            // Spread depth widely so users feel a real strength jump:
            //   Easy=1 (no opponent-response prediction → easy to trap)
            //   Medium=3 (sees player + own follow-up)
            //   Hard=5 (deep tactical calculation; slower thinks)
            val options = listOf(
                1 to stringResource(Res.string.mini_chess_difficulty_easy),
                3 to stringResource(Res.string.mini_chess_difficulty_medium),
                5 to stringResource(Res.string.mini_chess_difficulty_hard),
            )
            options.forEachIndexed { index, (depth, label) ->
                SegmentedButton(
                    selected = selected == depth,
                    onClick = {
                        selected = depth
                        onSelected(depth)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                    modifier = Modifier.hoverHand(),
                ) {
                    Text(label)
                }
            }
        }
    }
}
