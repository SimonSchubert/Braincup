package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName
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
        Spacer(LayoutHeight(8.dp))
        Subtitle1(text = description, modifier = LayoutGravity.Center)
        Spacer(LayoutHeight(16.dp))
        games.forEach {
            Spacer(LayoutHeight(16.dp))
            Row(modifier = LayoutGravity.Center) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    showInstructions(it)
                }
                val highscore = storage.getHighScore(it.getId())
                if (highscore > 0) {
                    Spacer(LayoutWidth(8.dp))
                    ImageButton(
                        drawableResource = it.getAndroidMedalResource
                            (highscore)
                    ) {
                        showScore(it)
                    }
                }
            }
        }

        Row(modifier = LayoutGravity.Center) {
            if (appOpenCount > 1) {
                PentagonStatistic(
                    title = "Training days",
                    value = appOpenCount.toString(),
                    modifier = LayoutGravity.Center
                )
            }
            if (totalScore > 0) {
                PentagonStatistic(
                    title = "Total score",
                    value = totalScore.toString(),
                    modifier = LayoutGravity.Center
                )
            }
        }

        Spacer(LayoutHeight(16.dp))
        TextImageButton(
            text = "Achievements (${storage.getUnlockedAchievements().size}/${UserStorage.Achievements.values().size})",
            drawableResource = R.drawable.ic_icons8_test_passed,
            modifier = LayoutGravity.Center
        ) {
            showAchievements()
        }

        Spacer(LayoutHeight(16.dp))
        TextImageButton(
            text = "Create challenge",
            drawableResource = R.drawable.ic_icons8_create_new3,
            modifier = LayoutGravity.Center,
            color = getComposeColor("#5c8e58")
        ) {
            createChallenge()
        }

        VectorImage(id = R.drawable.ic_waiting, modifier = LayoutGravity.Center)
    }
}


@Composable
fun PentagonStatistic(title: String, value: String, modifier: Modifier) {
    val vector = vectorResource(R.drawable.ic_icons8_pentagon)
    Container(
        modifier = modifier + LayoutSize(
            vector.defaultWidth,
            vector.defaultHeight
        )
    ) {
        DrawVector(vector)
        Column {
            Spacer(LayoutHeight(14.dp))
            Subtitle1(title, modifier = LayoutGravity.Center)
            Headline6(text = value, modifier = LayoutGravity.Center)
        }
    }
}