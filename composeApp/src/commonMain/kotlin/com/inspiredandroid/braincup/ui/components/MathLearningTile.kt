package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.math_learning_button
import braincup.composeapp.generated.resources.math_learning_subtitle
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.stringResource

@Composable
fun MathLearningTile(
    passedCount: Int,
    totalTopics: Int,
    onClick: () -> Unit,
) {
    PrismTile(
        face = Primary,
        modifier = Modifier
            .fillMaxWidth()
            .hoverHand(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.math_learning_button),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.math_learning_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
            Spacer(Modifier.width(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (passedCount > 0) {
                    PrismTrophy(
                        tint = MedalGold,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = "$passedCount/$totalTopics",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
            }
        }
    }
}
