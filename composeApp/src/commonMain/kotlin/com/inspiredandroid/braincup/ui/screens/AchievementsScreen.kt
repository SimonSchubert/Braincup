package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun AchievementsScreen(
    storage: UserStorage,
    onBack: () -> Unit,
) {
    val unlockedAchievements = remember(storage) { storage.getUnlockedAchievements() }
    val allAchievements = UserStorage.Achievements.entries

    AppScaffold(
        title = stringResource(Res.string.achievements_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(allAchievements, key = { it.name }) { achievement ->
                val isUnlocked = unlockedAchievements.contains(achievement)
                AchievementCard(
                    achievement = achievement,
                    isUnlocked = isUnlocked,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(),
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
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isUnlocked) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = contentColorFor(containerColor)
    PrismCard(
        face = containerColor,
        modifier = modifier,
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
                    color = contentColor,
                )
                Text(
                    text = stringResource(achievement.descriptionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
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
