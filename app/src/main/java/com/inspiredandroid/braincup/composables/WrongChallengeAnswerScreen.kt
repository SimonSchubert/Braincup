package com.inspiredandroid.braincup.composables

import android.content.Context
import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Spacing
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

@Composable
fun WrongChallengeAnswerScreen(
    context: Context, url: String,
    gameMaster: NavigationController
) {
    BaseApp {
        Headline5(text = "Unsolved", modifier = Gravity.Center wraps Spacing(16.dp))
        Subtitle2(text = "The challenge will stay unsolved for now.", modifier = Gravity.Center)
        VectorImage(id = R.drawable.ic_searching, modifier = Gravity.Center)
        TextImageButton(
            text = "Share challenge",
            drawableResource = R.drawable.ic_icons8_copy_link,
            onClick = { shareText(context, url) }, modifier = Gravity.Center
        )
        HeightSpacer(16.dp)
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            onClick = { gameMaster.start() },
            modifier = Gravity.Center
        )
    }
}