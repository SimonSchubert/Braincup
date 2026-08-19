package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_goal_label
import com.inspiredandroid.braincup.ui.theme.numeric
import org.jetbrains.compose.resources.stringResource

/**
 * "GOAL" over the target number, shared by the calculation games that ask you to reach it and by
 * their tutorial demos. The label stays in the Bungee display face while the number switches to
 * the readable number font a size up, so the target reads as a number to hit rather than as part
 * of the label.
 */
@Composable
fun GoalHeader(value: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.game_goal_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.toString(),
            style = if (LocalIsCompactHeight.current) {
                MaterialTheme.typography.headlineLarge.numeric()
            } else {
                MaterialTheme.typography.displaySmall.numeric()
            },
        )
    }
}
