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
import com.inspiredandroid.braincup.games.formattedScore
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.BrandedCard
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.components.XpAndLevelDisplay
import com.inspiredandroid.braincup.ui.theme.MedalBronze
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.MedalSilver
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
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
    ) {
        val medalTint = when {
            gameType.meetsScore(score, gameType.goldScore) -> MedalGold
            gameType.meetsScore(score, gameType.silverScore) -> MedalSilver
            score > 0 -> MedalBronze
            else -> null
        }

        if (medalTint != null) {
            PrismTrophy(
                tint = medalTint,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(8.dp))
        }

        // Level-based games suppress the score/highscore lines on give-up (score == 0),
        // since "Level: 0" / "Best Level: N" reads as misleading status info.
        val gaveUpLevelGame = gameType.usesLevelLabel && score <= 0
        if (!gaveUpLevelGame) {
            val scoreLabelRes = if (gameType.usesLevelLabel) Res.string.finish_level else Res.string.finish_score
            Text(
                text = stringResource(scoreLabelRes, gameType.formattedScore(score)),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(Modifier.height(16.dp))
        }

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
            BrandedCard(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.finish_new_highscore),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnPrimaryContainer,
                )
            }
        } else if (highscore > 0 && !gameType.usesLevelLabel) {
            Text(
                text = stringResource(Res.string.finish_highscore, gameType.formattedScore(highscore)),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        XpAndLevelDisplay(xpGained = xpGained, levelChange = levelChange)

        Spacer(Modifier.height(16.dp))

        PrimaryActionButton(
            onClick = onPlayRandom,
            value = stringResource(Res.string.button_play_random),
        )

        Spacer(Modifier.height(8.dp))

        val playAgainLabelRes = if (gameType.usesLevelLabel && !gaveUpLevelGame) {
            Res.string.button_play_next_level
        } else {
            Res.string.button_play_again
        }
        PrimaryActionButton(
            onClick = onPlayAgain,
            value = stringResource(playAgainLabelRes),
        )
    }
}
