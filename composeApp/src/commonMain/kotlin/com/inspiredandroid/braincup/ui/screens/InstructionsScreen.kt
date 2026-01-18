package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getDescription
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.ui.components.AppScaffold

@Composable
fun InstructionsScreen(
    gameType: GameType,
    onStart: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = gameType.getName(),
        onBack = onBack,
        scrollable = false,
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = gameType.getDescription(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Start")
        }
    }
}
