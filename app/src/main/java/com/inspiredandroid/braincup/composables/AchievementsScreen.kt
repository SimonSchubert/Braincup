package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.res.vectorResource
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.games.getDescription
import com.inspiredandroid.braincup.getAndroidResource
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun AchievementsScreen(
    allAchievements: List<UserStorage.Achievements>,
    unlockedAchievements: List<UserStorage.Achievements>,
    gameMaster: AppController
) {
    BaseScrollApp(
        title = "Achievements (${unlockedAchievements.size}/${allAchievements.size})",
        back = { gameMaster.start() }) {
        allAchievements.forEach {
            val vectorAsset = +vectorResource(it.getAndroidResource())
            if (unlockedAchievements.contains(it)) {
                ListItem(
                    text = { Text("${it.getDescription()} √") },
                    icon = {
                        DrawVector(
                            vectorAsset,
                            tintColor = com.inspiredandroid.braincup.games.tools.Color.GREEN.getComposeColor()
                        )
                    })
            } else {
                ListItem(text = { Text(it.getDescription()) }, icon = { DrawVector(vectorAsset) })
            }
            Divider()
        }
    }
}
