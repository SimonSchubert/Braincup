package com.inspiredandroid.braincup.composables

import android.content.Context
import androidx.compose.Composable
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Spacer
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

@Composable
fun WrongChallengeAnswerScreen(
    context: Context, url: String,
    gameMaster: NavigationController
) {
    BaseApp {
        Headline5(text = "Unsolved", modifier = LayoutGravity.Center + LayoutPadding(16.dp))
        Subtitle2(
            text = "The challenge will stay unsolved for now.",
            modifier = LayoutGravity.Center
        )
        VectorImage(id = R.drawable.ic_searching, modifier = LayoutGravity.Center)
        TextImageButton(
            text = "Share challenge",
            drawableResource = R.drawable.ic_icons8_copy_link,
            onClick = { shareText(context, url) }, modifier = LayoutGravity.Center
        )
        Spacer(LayoutHeight(16.dp))
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            onClick = { gameMaster.start() },
            modifier = LayoutGravity.Center
        )
    }
}