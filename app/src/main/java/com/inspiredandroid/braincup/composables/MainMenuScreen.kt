package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.paint
import androidx.ui.foundation.Box
import androidx.ui.graphics.vector.VectorPainter
import androidx.ui.graphics.vector.drawVector
import androidx.ui.layout.*
import androidx.ui.layout.ColumnScope.gravity
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
        Spacer(Modifier.preferredHeight(8.dp))
        Subtitle1(
            text = description,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.preferredHeight(16.dp))
        games.forEach {
            Spacer(Modifier.preferredHeight(16.dp))
            Row(modifier = Modifier.gravity(align = Alignment.CenterHorizontally)) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    android.os.Handler().post {
                        showInstructions(it)
                    }
                }
                val highscore = storage.getHighScore(it.getId())
                if (highscore > 0) {
                    Spacer(Modifier.preferredWidth(8.dp))
                    ImageButton(
                        drawableResource = it.getAndroidMedalResource
                            (highscore)
                    ) {
                        android.os.Handler().post {
                            showScore(it)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.gravity(align = Alignment.CenterHorizontally)) {
            if (appOpenCount > 1) {
                PentagonStatistic(
                    title = "Training days",
                    value = appOpenCount.toString(),
                    modifier = Modifier.gravity(align = Alignment.CenterVertically)
                )
            }
            if (totalScore > 0) {
                PentagonStatistic(
                    title = "Total score",
                    value = totalScore.toString(),
                    modifier = Modifier.gravity(align = Alignment.CenterVertically)
                )
            }
        }

        Spacer(Modifier.preferredHeight(16.dp))
        TextImageButton(
            text = "Achievements (${storage.getUnlockedAchievements().size}/${UserStorage.Achievements.values().size})",
            drawableResource = R.drawable.ic_icons8_test_passed,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        ) {
            android.os.Handler().post {
                showAchievements()
            }
        }

        Spacer(Modifier.preferredHeight(16.dp))
        TextImageButton(
            text = "Create challenge",
            drawableResource = R.drawable.ic_icons8_create_new3,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally),
            color = getComposeColor("#5c8e58")
        ) {
            android.os.Handler().post {
                createChallenge()
            }
        }

        VectorImage(
            id = R.drawable.ic_waiting,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun PentagonStatistic(title: String, value: String, modifier: Modifier) {
    val vector = vectorResource(R.drawable.ic_icons8_pentagon)
    val background = VectorPainter(asset = vector)
    Box(
        modifier = modifier + Modifier.preferredSize(
            vector.defaultWidth,
            vector.defaultHeight
        ) + Modifier.paint(background)
    ) {
        Column {
            Spacer(Modifier.preferredHeight(14.dp))
            Subtitle1(title, modifier = Modifier.gravity(align = Alignment.CenterHorizontally))
            Headline6(
                text = value,
                modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
            )
        }
    }
}