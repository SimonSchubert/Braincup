package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Modifier
import androidx.ui.core.WithDensity
import androidx.ui.core.dp
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.res.vectorResource
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.getAndroidDrawable
import com.inspiredandroid.braincup.getAndroidMedalResource
import com.inspiredandroid.braincup.getComposeColor

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
        Subtitle1(text = description, modifier = Gravity.Center)
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

        Row(modifier = Gravity.Center) {
            if (appOpenCount > 1) {
                PentagonStatistic(
                    title = "Training days",
                    value = appOpenCount.toString(),
                    modifier = Gravity.Center
                )
            }
            if (totalScore > 0) {
                PentagonStatistic(
                    title = "Total score",
                    value = totalScore.toString(),
                    modifier = Gravity.Center
                )
            }
        }

        HeightSpacer(16.dp)
        TextImageButton(
            text = "Achievements (${storage.getUnlockedAchievements().size}/${UserStorage.Achievements.values().size})",
            drawableResource = R.drawable.ic_icons8_test_passed,
            modifier = Gravity.Center
        ) {
            showAchievements()
        }
        HeightSpacer(16.dp)
        TextImageButton(
            text = "Create challenge",
            drawableResource = R.drawable.ic_icons8_hammer,
            modifier = Gravity.Center,
            color = Color.GREEN.getComposeColor()
        ) {
            createChallenge()
        }

        VectorImage(id = R.drawable.ic_waiting, modifier = Gravity.Center)
    }
}


@Composable
fun PentagonStatistic(title: String, value: String, modifier: Modifier) {
    val vector = +vectorResource(R.drawable.ic_icons8_pentagon)
    WithDensity {
        Container(
            modifier = modifier wraps Size(
                vector.defaultWidth.toDp(),
                vector.defaultHeight.toDp()
            )
        ) {
            DrawVector(vector)
            Column {
                HeightSpacer(height = 14.dp)
                Subtitle1(title, modifier = Gravity.Center)
                Headline6(text = value, modifier = Gravity.Center)
            }
        }
    }
}