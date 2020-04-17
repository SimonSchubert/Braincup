package com.inspiredandroid.braincup.composables

import android.content.Context
import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

@Composable
fun WrongChallengeAnswerScreen(
    context: Context, url: String,
    gameMaster: NavigationController
) {
    BaseApp {
        Headline5(
            text = "Unsolved",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally) + Modifier.padding(16.dp)
        )
        Subtitle2(
            text = "The challenge will stay unsolved for now.",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        VectorImage(
            id = R.drawable.ic_searching,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        TextImageButton(
            text = "Share challenge",
            drawableResource = R.drawable.ic_icons8_copy_link,
            onClick = { shareText(context, url) },
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.preferredHeight(16.dp))
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            onClick = {
                Handler().post {
                    gameMaster.start()
                }
                Unit
            },
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
    }
}