package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.layout.*
import androidx.ui.unit.dp
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
            modifier = LayoutGravity.Center + LayoutPadding(16.dp)
        )
        games.forEach {
            Spacer(LayoutHeight(16.dp))
            Row(modifier = LayoutGravity.Center) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    answer(it)
                }
            }
        }
    }
}