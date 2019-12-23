package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.MaterialTheme
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
    showInstructions: (GameType) -> Unit,
    showScore: (GameType) -> Unit,
    showAchievements: () -> Unit,
    createChallenge: () -> Unit,
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
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    showInstructions(it)
                }
                val highscore = storage.getHighScore(it.getId())
                if (highscore > 0) {
                    WidthSpacer(8.dp)
                    ImageButton(
                        drawableResource = it.getAndroidMedalResource
                            (highscore)
                    ) {
                        showScore(it)
                    }
                }
            }
        }
        if (appOpenCount > 0) {
            HeightSpacer(32.dp)
            Text(
                "Training days",
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

        TextImageButton(
            text = "Achievements (${storage.getUnlockedAchievements().size}/${UserStorage.Achievements.values().size})",
            drawableResource = R.drawable.ic_icons8_test_passed,
            modifier = Gravity.Center
        ) {
            showAchievements()
        }

        VectorImage(id = R.drawable.ic_waiting, modifier = Gravity.Center)
    }
}
