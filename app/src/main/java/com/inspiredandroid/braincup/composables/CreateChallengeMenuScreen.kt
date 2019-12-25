package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.getAndroidDrawable

@Composable
fun CreateChallengeMenuScreen(
    games: List<GameType>,
    answer: (GameType) -> Unit,
    gameMaster: NavigationController
) {
    BaseApp(title = "Create challenge", back = { gameMaster.start() }) {
        Subtitle1(
            text = "Create your own challenge and share it with your friends, family and co-workers. You can also hide a secret message which will get unveiled after solving the challenge.",
            modifier = Gravity.Center wraps Spacing(16.dp)
        )
        games.forEach {
            HeightSpacer(16.dp)
            Row(modifier = Gravity.Center) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    answer(it)
                }
            }
        }
    }
}