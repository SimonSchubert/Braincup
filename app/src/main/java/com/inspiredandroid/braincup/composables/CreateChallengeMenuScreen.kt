package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    BaseApp(title = "Create challenge", back = {
        Handler().post {
            gameMaster.start()
        }
        Unit
    }) {
        Subtitle1(
            text = "Create your own challenge and share it with your friends, family and co-workers. You can also hide a secret message which will get unveiled after solving the challenge.",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        )
        games.forEach {
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    Handler().post {
                        answer(it)
                    }
                }
            }
        }
    }
}