package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.ui.components.AppScaffold
import org.jetbrains.compose.resources.stringResource

@Composable
fun AchievementsScreen(
    storage: UserStorage,
    onBack: () -> Unit,
) {
    val unlockedAchievements = storage.getUnlockedAchievements()
    val allAchievements = UserStorage.Achievements.entries

    AppScaffold(
        title = stringResource(Res.string.achievements_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(allAchievements, key = { it.name }) { achievement ->
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
    val containerColor = if (isUnlocked) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColorFor(containerColor),
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
                )
            }
            if (isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    modifier = Modifier.size(32.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
