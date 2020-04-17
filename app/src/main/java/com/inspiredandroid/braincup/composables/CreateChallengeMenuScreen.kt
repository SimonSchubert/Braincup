package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
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
    BaseApp(title = "Create challenge", back = {
        Handler().post {
            gameMaster.start()
        }
        Unit
    }) {
        Subtitle1(
            text = "Create your own challenge and share it with your friends, family and co-workers. You can also hide a secret message which will get unveiled after solving the challenge.",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally) + Modifier.padding(16.dp)
        )
        games.forEach {
            Spacer(Modifier.preferredHeight(16.dp))
            Row(modifier = Modifier.gravity(align = Alignment.CenterHorizontally)) {
                TextImageButton(text = it.getName(), drawableResource = it.getAndroidDrawable()) {
                    Handler().post {
                        answer(it)
                    }
                }
            }
        }
    }
}