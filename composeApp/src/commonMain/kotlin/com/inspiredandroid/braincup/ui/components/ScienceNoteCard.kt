package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.science_note_based_on
import com.inspiredandroid.braincup.games.GameScience
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import org.jetbrains.compose.resources.stringResource

/**
 * The research note under a demo, for the games that implement a published cognitive task.
 *
 * Three lines: what the game is based on, the paper it comes from, and what the task measures.
 * The card states the provenance and stops there, making no claim about what playing it does for
 * the player, which is the only claim the literature would not support (these tasks are validated
 * as *measures*; far transfer from practising them is not established).
 */
@Composable
fun ScienceNoteCard(
    science: GameScience,
    modifier: Modifier = Modifier,
) {
    BrandedCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.science_note_based_on, stringResource(science.paradigmRes)),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnPrimaryContainer,
        )
        Spacer(Modifier.height(2.dp))
        // Directly under the name, because name over source is how a reference reads.
        Text(
            text = science.citation,
            style = MaterialTheme.typography.labelMedium,
            color = OnPrimaryContainer.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(science.summaryRes),
            style = MaterialTheme.typography.bodyMedium,
            color = OnPrimaryContainer,
        )
    }
}
