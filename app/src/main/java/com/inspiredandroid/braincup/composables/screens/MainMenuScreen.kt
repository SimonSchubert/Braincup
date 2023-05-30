package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.composables.BaseScrollApp
import com.inspiredandroid.braincup.composables.Cell
import com.inspiredandroid.braincup.composables.Headline6
import com.inspiredandroid.braincup.composables.ImageButton
import com.inspiredandroid.braincup.composables.Subtitle1
import com.inspiredandroid.braincup.composables.TextImageButton
import com.inspiredandroid.braincup.composables.VectorImage
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
    BaseScrollApp(title, Modifier.padding(horizontal = 24.dp).widthIn(max = 400.dp)) {

        Spacer(Modifier.height(8.dp))

        VectorImage(
            id = R.drawable.ic_waiting,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        games.forEach {
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(
                    modifier = Modifier.weight(1f, fill = true).padding(horizontal = 8.dp),
                    propagateMinConstraints = true
                ) {
                    TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                        showInstructions(it)
                    }
                }
                Box(
                    modifier = Modifier.size(48.dp),
                    propagateMinConstraints = true
                ) {
                    val highscore = storage.getHighScore(it.getId())
                    if (highscore > 0) {
                        ImageButton(
                            drawableResource = it.getAndroidMedalResource
                                (highscore)
                        ) {
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
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
        ) {
            showAchievements()
        }

        Spacer(Modifier.height(16.dp))

        TextImageButton(
            text = "Create challenge",
            drawableResource = R.drawable.ic_icons8_create_new3,
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
            color = getComposeColor("#5c8e58")
        ) {
            createChallenge()
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun PentagonStatistic(title: String, value: String, modifier: Modifier) {
    val background = painterResource(R.drawable.ic_icons8_pentagon)
    Box(
        modifier = modifier
    ) {
        Image(
            painter = background,
            contentDescription = null,
        )
        Column(modifier = Modifier
            .align(Alignment.Center)
            .offset(0.dp, 4.dp)) {
            Spacer(Modifier.height(14.dp))
            Subtitle1(title, modifier = Modifier.align(Alignment.CenterHorizontally))
            Headline6(
                text = value,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}