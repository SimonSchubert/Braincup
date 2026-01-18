package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.ui.components.AppScaffold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AchievementsScreen(
    storage: UserStorage,
    onBack: () -> Unit,
) {
    val unlockedAchievements = storage.getUnlockedAchievements()
    val allAchievements = UserStorage.Achievements.entries.drop(3)

    AppScaffold(
        title = stringResource(Res.string.achievements_title),
        onBack = onBack,
        scrollable = false,
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.achievements_unlocked, unlockedAchievements.size, allAchievements.size),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            items(allAchievements) { achievement ->
                val isUnlocked = unlockedAchievements.contains(achievement)
                AchievementCard(
                    achievement = achievement,
                    isUnlocked = isUnlocked,
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: UserStorage.Achievements,
    isUnlocked: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(achievement.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(achievement.descriptionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isUnlocked) {
                Icon(
                    painterResource(Res.drawable.ic_baseline_check_24),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
