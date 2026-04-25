package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.LevelUpBanner
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.theme.MedalBronze
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.MedalSilver
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FinishScreen(
    gameType: GameType,
    score: Int,
    isNewHighscore: Boolean,
    answeredAllCorrect: Boolean,
    highscore: Int,
    xpGained: Int,
    totalXpAfter: Int,
    onPlayRandom: () -> Unit,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
) {
    val levelAfter = UserStorage.levelForXp(totalXpAfter)
    val levelBefore = UserStorage.levelForXp(totalXpAfter - xpGained)
    val levelChange = if (levelAfter > levelBefore) {
        UserStorage.LevelChange(
            oldLevel = levelBefore,
            newLevel = levelAfter,
            totalXpBefore = totalXpAfter - xpGained,
            totalXpAfter = totalXpAfter,
        )
    } else {
        null
    }
    AppScaffold(
        title = stringResource(gameType.displayNameRes),
        onBack = onMenu,
        scrollable = false,
    ) {
        val medalTint = when {
            gameType.meetsScore(score, gameType.goldScore) -> MedalGold
            gameType.meetsScore(score, gameType.silverScore) -> MedalSilver
            score > 0 -> MedalBronze
            else -> null
        }

        if (medalTint != null) {
            Icon(
                painterResource(Res.drawable.ic_icons8_counter_gold),
                contentDescription = null,
                tint = medalTint,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(8.dp))
        }

        Text(
            text = stringResource(Res.string.finish_score, gameType.formatScore(score)),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        if (answeredAllCorrect && !gameType.lowerScoreIsBetter) {
            Text(
                text = stringResource(Res.string.finish_bonus_point),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }

        if (isNewHighscore) {
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryContainer,
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(Res.string.finish_new_highscore),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        } else if (highscore > 0) {
            Text(
                text = stringResource(Res.string.finish_highscore, gameType.formatScore(highscore)),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        if (xpGained > 0) {
            Spacer(Modifier.height(16.dp))
            XpGainedChip(
                xpGained = xpGained,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        if (levelChange != null) {
            Spacer(Modifier.height(16.dp))
            LevelUpBanner(
                levelChange = levelChange,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            onClick = onPlayRandom,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = stringResource(Res.string.button_play_random),
        )

        Spacer(Modifier.height(8.dp))

        DefaultButton(
            onClick = onPlayAgain,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = stringResource(Res.string.button_play_again),
        )
    }
}
