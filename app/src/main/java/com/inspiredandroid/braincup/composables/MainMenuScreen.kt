package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        Spacer(Modifier.height(8.dp))
        Subtitle1(
            text = description,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        games.forEach {
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    android.os.Handler().post {
                        showInstructions(it)
                    }
                }
                val highscore = storage.getHighScore(it.getId())
                if (highscore > 0) {
                    Spacer(Modifier.width(8.dp))
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

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            if (appOpenCount > 1) {
                PentagonStatistic(
                    title = "Training days",
                    value = appOpenCount.toString(),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            if (totalScore > 0) {
                PentagonStatistic(
                    title = "Total score",
                    value = totalScore.toString(),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        TextImageButton(
            text = "Achievements (${storage.getUnlockedAchievements().size}/${UserStorage.Achievements.values().size})",
            drawableResource = R.drawable.ic_icons8_test_passed,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            android.os.Handler().post {
                showAchievements()
            }
        }

        Spacer(Modifier.height(16.dp))
        TextImageButton(
            text = "Create challenge",
            drawableResource = R.drawable.ic_icons8_create_new3,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = getComposeColor("#5c8e58")
        ) {
            android.os.Handler().post {
                createChallenge()
            }
        }

        VectorImage(
            id = R.drawable.ic_waiting,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun PentagonStatistic(title: String, value: String, modifier: Modifier) {
    val background = painterResource(R.drawable.ic_icons8_pentagon)
    Box(
        modifier = modifier
    ) {
        Image(painter = background,
            contentDescription = null,
        )
        Column(modifier = Modifier.align(Alignment.Center).offset(0.dp, 4.dp)) {
            Spacer(Modifier.height(14.dp))
            Subtitle1(title, modifier = Modifier.align(Alignment.CenterHorizontally))
            Headline6(
                text = value,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}