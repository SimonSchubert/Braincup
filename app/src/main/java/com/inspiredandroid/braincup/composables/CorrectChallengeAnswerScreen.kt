package com.inspiredandroid.braincup.composables

import android.content.Context
import android.content.Intent
import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Spacing
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController

@Composable
fun CorrectChallengeAnswerScreen(
    context: Context, solution: String?, secret: String, url: String,
    gameMaster: NavigationController
) {
    BaseScrollApp {
        Headline5(text = "Congratulation", modifier = Gravity.Center wraps Spacing(16.dp))
        Subtitle2(
            text = "Your solution '$solution' solved the challenge.",
            modifier = Gravity.Center
        )
        if (secret.isNotEmpty()) {
            HeightSpacer(height = 24.dp)
            Headline6(text = "Secret unveiled: $secret", modifier = Gravity.Center)
        }
        VectorImage(id = R.drawable.ic_delivery, modifier = Gravity.Center)
        TextImageButton(
            text = "Share challenge",
            drawableResource = R.drawable.ic_icons8_copy_link,
            onClick = { shareText(context, url) },
            modifier = Gravity.Center
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

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
