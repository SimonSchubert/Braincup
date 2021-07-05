package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.games.getDescription

@Composable
fun AchievementsScreen(
    allAchievements: List<UserStorage.Achievements>,
    unlockedAchievements: List<UserStorage.Achievements>,
    gameMaster: NavigationController
) {
    BaseScrollApp(
        title = "Achievements (${unlockedAchievements.size}/${allAchievements.size})",
        back = {
            Handler().post {
                gameMaster.start()
            }
            Unit
        }) {
        allAchievements.forEach {
            if (unlockedAchievements.contains(it)) {
                ListItem(
                    text = { Text(it.getDescription()) },
                    icon = { VectorImage(id = R.drawable.ic_icons8_medal_first_place) },
                    trailing = { VectorImage(id = R.drawable.ic_baseline_check_24) })
            } else {
                ListItem(
                    text = { Text(it.getDescription()) },
                    icon = {
                        VectorImage(
                            id = R.drawable.ic_icons8_medal_first_place,
                            tint = Color.Black
                        )
                    })
            }
            Divider()
        }
    }
}