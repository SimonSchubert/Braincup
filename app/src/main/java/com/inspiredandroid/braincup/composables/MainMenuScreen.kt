package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.getAndroidDrawable
import com.inspiredandroid.braincup.getAndroidMedalResource

@Composable
fun MainMenuScreen(
    title: String,
    description: String,
    games: List<GameType>,
    instructions: (GameType) -> Unit,
    score: (GameType) -> Unit,
    achievements: () -> Unit,
    storage: UserStorage,
    totalScore: Int,
    appOpenCount: Int
) {
    BaseScrollApp(title) {
        HeightSpacer(8.dp)
        Text(
            description,
            style = (+MaterialTheme.typography()).subtitle1,
            modifier = Gravity.Center
        )
        HeightSpacer(16.dp)
        games.forEach {
            HeightSpacer(16.dp)
            Row(modifier = Gravity.Center) {
                Button(onClick = { instructions(it) }) {
                    Row {
                        val vectorAsset =
                            +vectorResource(it.getAndroidDrawable())
                        Container(width = 24.dp, height = 24.dp) {
                            DrawVector(vectorAsset)
                        }
                        WidthSpacer(16.dp)
                        Text(text = it.getName())
                    }
                }
                val highscore = storage.getHighScore(it.getId())
                if (highscore > 0) {
                    WidthSpacer(8.dp)
                    Button(onClick = { score(it) }) {
                        val vectorAsset = +vectorResource(
                            it.getAndroidMedalResource
                                (highscore)
                        )
                        Container(width = 24.dp, height = 24.dp) {
                            DrawVector(vectorAsset)
                        }
                    }
                }
            }
        }
        if (appOpenCount > 0) {
            HeightSpacer(32.dp)
            Text(
                "Consecutive training",
                style = (+MaterialTheme.typography()).subtitle1,
                modifier = Gravity.Center
            )
            Text(
                appOpenCount.toString(),
                style = (+MaterialTheme.typography()).h6,
                modifier = Gravity.Center
            )
        }
        if (totalScore > 0) {
            HeightSpacer(16.dp)
            Text(
                "Total score",
                style = (+MaterialTheme.typography()).subtitle1,
                modifier = Gravity.Center
            )
            Text(
                totalScore.toString(),
                style = (+MaterialTheme.typography()).h6,
                modifier = Gravity.Center
            )
        }
        HeightSpacer(24.dp)
        Button(onClick = { achievements() }, modifier = Gravity.Center) {
            Row(
                arrangement = Arrangement.Center
            ) {
                val vectorAsset =
                    +vectorResource(R.drawable.ic_icons8_test_passed)
                Container(
                    width = 24.dp, height = 24.dp,
                    modifier = Gravity.Center
                ) {
                    DrawVector(vectorAsset)
                }
                WidthSpacer(16.dp)
                val unlockedAchievements =
                    storage.getUnlockedAchievements()
                Text(
                    text = "Achievements (${unlockedAchievements.size}/${UserStorage.Achievements.values().size})"
                )
            }
        }
        VectorImage(id = R.drawable.ic_waiting, modifier = Gravity.Center)
    }
}
