package com.inspiredandroid.braincup.composables

import android.content.Context
import android.content.Intent
import androidx.compose.Composable
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Spacer
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

@Composable
fun CorrectChallengeAnswerScreen(
    context: Context, solution: String?, secret: String, url: String,
    gameMaster: NavigationController
) {
    BaseScrollApp {
        Headline5(text = "Congratulation", modifier = LayoutGravity.Center + LayoutPadding(16.dp))
        Subtitle2(
            text = "Your solution '$solution' solved the challenge.",
            modifier = LayoutGravity.Center
        )
        if (secret.isNotEmpty()) {
            Spacer(LayoutHeight(24.dp))
            Headline6(text = "Secret unveiled: $secret", modifier = LayoutGravity.Center)
        }
        VectorImage(id = R.drawable.ic_delivery, modifier = LayoutGravity.Center)
        TextImageButton(
            text = "Share challenge",
            drawableResource = R.drawable.ic_icons8_copy_link,
            onClick = { shareText(context, url) },
            modifier = LayoutGravity.Center
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

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
