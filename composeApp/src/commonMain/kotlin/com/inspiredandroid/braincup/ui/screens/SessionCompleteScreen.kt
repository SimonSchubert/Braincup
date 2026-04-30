package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getGameTypeById
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.BrandedCard
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.XpAndLevelDisplay
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionCompleteScreen(
    gameIds: List<String>,
    scores: List<Int>,
    streakBefore: Int,
    streakAfter: Int,
    xpGained: Int,
    levelChange: UserStorage.LevelChange?,
    onDone: () -> Unit,
) {
    // Sum only points-based scores; time-based scores (lower-is-better) are in different units
    // and would make the total meaningless if added in.
    val total = gameIds.zip(scores).filter { (id, _) ->
        getGameTypeById(id)?.lowerScoreIsBetter != true
    }.sumOf { (_, score) -> score }
    val games = gameIds.mapNotNull { getGameTypeById(it) }
    val streakIncreased = streakAfter > streakBefore

    AppScaffold(
        title = stringResource(Res.string.session_complete_title),
        onBack = onDone,
        scrollable = true,
    ) {
        Image(
            painterResource(Res.drawable.ic_success),
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .align(Alignment.CenterHorizontally),
        )

        Text(
            text = stringResource(Res.string.session_total_score, total),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        if (streakIncreased) {
            BrandedCard(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.session_streak_increased, streakAfter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else if (streakAfter > 0) {
            Text(
                text = stringResource(Res.string.session_streak_current, streakAfter),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        XpAndLevelDisplay(xpGained = xpGained, levelChange = levelChange)

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            games.forEachIndexed { index, game ->
                SessionGameRow(
                    game = game,
                    score = scores.getOrNull(index) ?: 0,
                )
                if (index < games.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        PrimaryActionButton(
            onClick = onDone,
            value = stringResource(Res.string.session_done),
        )
    }
}

@Composable
private fun SessionGameRow(game: GameType, score: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = Color(game.accentColor),
                    shape = RoundedCornerShape(6.dp),
                ),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(game.displayNameRes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = game.formatScore(score),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
