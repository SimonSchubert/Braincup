package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import com.inspiredandroid.braincup.app.NO_CONGRUENCY_EFFECT
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.formattedScore
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.BrandedCard
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.ChunkyCross
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.components.XpAndLevelDisplay
import com.inspiredandroid.braincup.ui.components.sectionWidth
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.medalTint
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
    adaptiveStartRoundCredit: Int = 0,
    isFinalCatalogLevel: Boolean = false,
    targetsFound: Int = -1,
    targetsTotal: Int = -1,
    mistakes: Int = -1,
    congruencyEffectMs: Int = NO_CONGRUENCY_EFFECT,
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
        val medalTint = gameType.medalTint(score)

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
            val baseScore = (score - adaptiveStartRoundCredit).coerceAtLeast(0)
            val scoreLabelRes = when {
                gameType.usesLevelLabel -> Res.string.finish_level
                gameType.usesTriesLabel -> Res.string.finish_tries
                else -> Res.string.finish_score
            }
            Text(
                text = stringResource(scoreLabelRes, gameType.formattedScore(baseScore)),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            if (adaptiveStartRoundCredit > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        Res.string.finish_difficulty_bonus,
                        gameType.formattedScore(adaptiveStartRoundCredit),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (targetsFound >= 0 && targetsTotal > 0 && mistakes >= 0) {
            TargetBlockSummary(
                found = targetsFound,
                total = targetsTotal,
                mistakes = mistakes,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(16.dp))
        }

        if (congruencyEffectMs != NO_CONGRUENCY_EFFECT) {
            CongruencyEffectCard(millis = congruencyEffectMs)
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
                modifier = sectionWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        if (gameType.usesTriesLabel) {
                            Res.string.finish_new_best_tries
                        } else {
                            Res.string.finish_new_highscore
                        },
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnPrimaryContainer,
                )
            }
        } else if (highscore > 0 && !gameType.usesLevelLabel) {
            Text(
                text = stringResource(
                    if (gameType.usesTriesLabel) {
                        Res.string.finish_best_tries
                    } else {
                        Res.string.finish_highscore
                    },
                    gameType.formattedScore(highscore),
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        XpAndLevelDisplay(xpGained = xpGained, levelChange = levelChange)

        if (isFinalCatalogLevel) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.finish_max_level_reached),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        PrimaryActionButton(
            onClick = onPlayRandom,
            value = stringResource(Res.string.button_play_random),
        )

        if (!isFinalCatalogLevel) {
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
}

/**
 * The reading a Stroop run produces, which the score does not carry: how much longer the trials
 * whose word disagreed with the ink took than the ones where the two agreed.
 *
 * Shown with a sign, and negative values are shown as they are rather than floored at zero. A run
 * can genuinely come out that way, and rounding it up to "no cost" would be inventing a result.
 */
@Composable
private fun ColumnScope.CongruencyEffectCard(millis: Int) {
    BrandedCard(
        modifier = sectionWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.finish_congruency_effect_title),
            style = MaterialTheme.typography.labelLarge,
            color = OnPrimaryContainer.copy(alpha = 0.7f),
        )
        Text(
            text = stringResource(
                Res.string.finish_congruency_effect_value,
                if (millis > 0) "+$millis" else "$millis",
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = OnPrimaryContainer,
        )
        Text(
            text = stringResource(Res.string.finish_congruency_effect_explainer),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = OnPrimaryContainer.copy(alpha = 0.7f),
        )
    }
}

/**
 * "4 of 6 found, 2 mistakes" as two numerals against a drawn tick and cross, so it needs no
 * translating. The two lines count different things on purpose: a wrong tap does not cost a
 * target, so a block can read 6 of 6 with a mistake against it. Both take their colour from
 * whether the run was a clean one, so they can never look like they disagree.
 */
@Composable
private fun TargetBlockSummary(
    found: Int,
    total: Int,
    mistakes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SummaryCount(
            correct = true,
            text = "$found/$total",
            tint = if (found == total) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SummaryCount(
            correct = false,
            text = "$mistakes",
            tint = if (mistakes == 0) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.error
            },
        )
    }
}

@Composable
private fun SummaryCount(correct: Boolean, text: String, tint: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (correct) {
            ChunkyCheck(tint, Modifier.size(20.dp))
        } else {
            ChunkyCross(tint, Modifier.size(20.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = tint,
        )
    }
}

@DevicePreviews
@Composable
private fun FinishScreenPreview() {
    ScreenPreviewHost {
        FinishScreen(
            gameType = GameType.MENTAL_CALCULATION,
            score = 12,
            isNewHighscore = true,
            answeredAllCorrect = false,
            highscore = 12,
            xpGained = 40,
            totalXpAfter = 240,
            onPlayRandom = {},
            onPlayAgain = {},
            onMenu = {},
        )
    }
}
