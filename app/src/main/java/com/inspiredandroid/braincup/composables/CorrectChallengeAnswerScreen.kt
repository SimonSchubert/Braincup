package com.inspiredandroid.braincup.composables

import android.content.Context
import android.content.Intent
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
fun CorrectChallengeAnswerScreen(
    context: Context, solution: String?, secret: String, url: String,
    gameMaster: NavigationController
) {
    BaseScrollApp {
        Headline5(
            text = "Congratulation",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally) + Modifier.padding(16.dp)
        )
        Subtitle2(
            text = "Your solution '$solution' solved the challenge.",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        if (secret.isNotEmpty()) {
            Spacer(Modifier.preferredHeight(24.dp))
            Headline6(
                text = "Secret unveiled: $secret",
                modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
            )
        }
        VectorImage(
            id = R.drawable.ic_delivery,
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
            onClick = { gameMaster.start() },
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
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
