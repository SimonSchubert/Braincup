package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton

@Composable
fun FinishScreen(
    gameType: GameType,
    score: Int,
    isNewHighscore: Boolean,
    answeredAllCorrect: Boolean,
    onPlayRandom: () -> Unit,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
) {
    AppScaffold(
        title = gameType.displayName,
        onBack = onMenu,
        scrollable = false,
    ) {
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        if (answeredAllCorrect) {
            Text(
                text = "You got 1 extra point for making zero mistakes!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }

        if (isNewHighscore) {
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "New Highscore!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            onClick = onPlayRandom,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = "Play Random Game",
        )

        Spacer(Modifier.height(8.dp))

        DefaultButton(
            onClick = onPlayAgain,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = "Play Again",
        )
    }
}
