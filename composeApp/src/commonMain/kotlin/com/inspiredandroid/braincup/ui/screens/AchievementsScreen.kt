package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.ui.components.AppScaffold

@Composable
fun AchievementsScreen(
    storage: UserStorage,
    onBack: () -> Unit,
) {
    val unlockedAchievements = storage.getUnlockedAchievements()
    val allAchievements = UserStorage.Achievements.entries.drop(3)

    AppScaffold(
        title = "Achievements",
        onBack = onBack,
        scrollable = false,
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = "${unlockedAchievements.size}/${allAchievements.size} Unlocked",
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
                    text = achievement.getTitle(),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = achievement.getDescription(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isUnlocked) {
                Text(
                    text = "\u2713",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private fun UserStorage.Achievements.getTitle(): String = when (this) {
    UserStorage.Achievements.MEDAL_BRONZE -> "Bronze Medal"
    UserStorage.Achievements.MEDAL_SILVER -> "Silver Medal"
    UserStorage.Achievements.MEDAL_GOLD -> "Gold Medal"
    UserStorage.Achievements.SCORES_10 -> "10 Points"
    UserStorage.Achievements.SCORES_100 -> "100 Points"
    UserStorage.Achievements.SCORES_1000 -> "1,000 Points"
    UserStorage.Achievements.SCORES_10000 -> "10,000 Points"
    UserStorage.Achievements.APP_OPEN_3 -> "3 Day Streak"
    UserStorage.Achievements.APP_OPEN_7 -> "7 Day Streak"
    UserStorage.Achievements.APP_OPEN_30 -> "30 Day Streak"
}

private fun UserStorage.Achievements.getDescription(): String = when (this) {
    UserStorage.Achievements.MEDAL_BRONZE -> "Score at least 1 point in all games"
    UserStorage.Achievements.MEDAL_SILVER -> "Reach silver score in all games"
    UserStorage.Achievements.MEDAL_GOLD -> "Reach gold score in all games"
    UserStorage.Achievements.SCORES_10 -> "Accumulate 10 total points"
    UserStorage.Achievements.SCORES_100 -> "Accumulate 100 total points"
    UserStorage.Achievements.SCORES_1000 -> "Accumulate 1,000 total points"
    UserStorage.Achievements.SCORES_10000 -> "Accumulate 10,000 total points"
    UserStorage.Achievements.APP_OPEN_3 -> "Train 3 days in a row"
    UserStorage.Achievements.APP_OPEN_7 -> "Train 7 days in a row"
    UserStorage.Achievements.APP_OPEN_30 -> "Train 30 days in a row"
}
